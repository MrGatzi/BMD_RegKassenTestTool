import { Hono } from "hono";
import { cors } from "hono/cors";
import { serve } from "@hono/node-server";
import { spawn } from "node:child_process";
import { mkdtemp, writeFile, rm } from "node:fs/promises";
import { tmpdir } from "node:os";
import path from "node:path";
import { createReadStream } from "node:fs";
import { randomUUID } from "node:crypto";
import { gunzipSync } from "node:zlib";

const app = new Hono();

app.use("*", cors());

const JAR_DIR = process.env.JAR_DIR || path.resolve(import.meta.dirname, "..");

function jarPath(name) {
  return path.join(JAR_DIR, name);
}

async function materialize(file, workspace, prefix) {
  const buf = Buffer.from(await file.arrayBuffer());
  const name = (file.name || "input.json").replace(/[^a-zA-Z0-9._-]/g, "_");
  let content = buf;
  if (name.endsWith(".gz")) {
    content = gunzipSync(buf);
  }
  const dest = path.join(workspace, `${prefix}-${name}`);
  await writeFile(dest, content);
  return dest;
}

app.get("/health", (c) => c.json({ ok: true, java: true }));

app.post("/verify/:type", async (c) => {
  const type = c.req.param("type");

  const jarMap = {
    dep: "regkassen-verification-depformat-1.1.1.jar",
    receipts: "regkassen-verification-receipts-1.1.1.jar",
  };

  if (!jarMap[type]) {
    return c.json({ error: `Unknown type: ${type}` }, 400);
  }

  const form = await c.req.formData();
  const inputFile = form.get("inputFile");
  const cryptoFile = form.get("cryptoFile");

  if (!inputFile || !cryptoFile) {
    return c.json({ error: "inputFile and cryptoFile are required" }, 400);
  }

  const allowFuture = form.get("allowFuture") === "true";
  const verbose = form.get("verbose") === "true";
  const heapMb = Math.max(256, Math.min(4096, Number(form.get("heapMb")) || 1500));

  const workspace = await mkdtemp(path.join(tmpdir(), "rksv-backend-"));

  try {
    const inputPath = await materialize(inputFile, workspace, "input");
    const cryptoPath = await materialize(cryptoFile, workspace, "crypto");
    const outputDir = path.join(workspace, "output");
    const { mkdirSync } = await import("node:fs");
    mkdirSync(outputDir, { recursive: true });

    const jar = jarPath(jarMap[type]);
    const args = [
      `-Xmx${heapMb}m`,
      "-jar",
      jar,
      ...(allowFuture ? ["-f"] : []),
      ...(verbose ? ["-v"] : []),
      "-i", inputPath,
      "-c", cryptoPath,
      "-o", outputDir,
    ];

    const stream = new ReadableStream({
      start(controller) {
        const enc = new TextEncoder();
        let closed = false;

        function send(event, data) {
          if (closed) return;
          controller.enqueue(enc.encode(`event: ${event}\ndata: ${JSON.stringify(data)}\n\n`));
        }

        function finish() {
          if (closed) return;
          closed = true;
          controller.close();
        }

        send("status", { phase: "starting", command: `java ${args.join(" ")}` });

        const child = spawn("java", args, { cwd: JAR_DIR });
        const startedAt = Date.now();

        child.stdout.on("data", (chunk) => {
          send("stdout", { text: chunk.toString() });
        });

        child.stderr.on("data", (chunk) => {
          send("stderr", { text: chunk.toString() });
        });

        child.on("close", async (exitCode) => {
          const durationMs = Date.now() - startedAt;
          send("done", { exitCode, durationMs });
          finish();
          await rm(workspace, { recursive: true, force: true }).catch(() => {});
        });

        child.on("error", async (err) => {
          send("error", { message: err.message });
          finish();
          await rm(workspace, { recursive: true, force: true }).catch(() => {});
        });
      },
    });

    return new Response(stream, {
      headers: {
        "Content-Type": "text/event-stream",
        "Cache-Control": "no-cache",
        "Connection": "keep-alive",
      },
    });
  } catch (err) {
    await rm(workspace, { recursive: true, force: true }).catch(() => {});
    return c.json({ error: err.message }, 500);
  }
});

const port = Number(process.env.PORT) || 4000;
console.log(`RKSV verification backend listening on :${port}`);
console.log(`JAR directory: ${JAR_DIR}`);
serve({ fetch: app.fetch, port });
