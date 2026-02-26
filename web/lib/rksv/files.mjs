import path from "node:path";
import { tmpdir } from "node:os";
import { randomUUID } from "node:crypto";
import { gunzipSync } from "node:zlib";
import {
  mkdtemp,
  readFile,
  readdir,
  rm,
  stat,
  writeFile,
} from "node:fs/promises";
import AdmZip from "adm-zip";

function sanitizeName(fileName) {
  return fileName.replace(/[^a-zA-Z0-9._-]+/g, "_");
}

function selectZipEntry(entries, purpose) {
  const hints = {
    dep: ["dep-export", "dep", "export"],
    receipts: ["qr-code", "receipt", "qr", "machine"],
    crypto: ["cryptographic", "crypto", "material"],
  }[purpose] ?? [purpose];

  const candidates = entries.filter((entry) => !entry.isDirectory);
  if (candidates.length === 0) {
    throw new Error("The provided ZIP archive is empty.");
  }

  let best = candidates[0];
  let bestScore = -1;

  for (const entry of candidates) {
    const normalized = entry.entryName.toLowerCase();
    let score = normalized.endsWith(".json") ? 10 : 0;
    for (const hint of hints) {
      if (normalized.includes(hint)) {
        score += 5;
      }
    }

    if (score > bestScore) {
      best = entry;
      bestScore = score;
    }
  }

  return best;
}

export async function createTempWorkspace(prefix = "rksv-web-") {
  const dir = await mkdtemp(path.join(tmpdir(), prefix));
  return dir;
}

export async function removeDirSafe(targetDir) {
  if (!targetDir) {
    return;
  }
  await rm(targetDir, { recursive: true, force: true });
}

export async function materializeUploadedFile(file, { tmpDir, purpose }) {
  if (!(file instanceof File)) {
    throw new Error(`Missing uploaded file for ${purpose}.`);
  }

  const sourceName = sanitizeName(file.name || `${purpose}.json`);
  const sourceBuffer = Buffer.from(await file.arrayBuffer());
  const lower = sourceName.toLowerCase();

  let contentBuffer = sourceBuffer;
  let extractedFrom = "raw";
  let outputName = sourceName;

  if (lower.endsWith(".gz")) {
    contentBuffer = gunzipSync(sourceBuffer);
    extractedFrom = "gzip";
    outputName = sourceName.replace(/\.gz$/i, "");
    if (!outputName.toLowerCase().endsWith(".json")) {
      outputName = `${outputName}.json`;
    }
  } else if (lower.endsWith(".zip")) {
    const zip = new AdmZip(sourceBuffer);
    const entry = selectZipEntry(zip.getEntries(), purpose);
    contentBuffer = entry.getData();
    extractedFrom = `zip:${entry.entryName}`;
    outputName = sanitizeName(path.basename(entry.entryName));
    if (!outputName.toLowerCase().endsWith(".json")) {
      outputName = `${outputName}.json`;
    }
  }

  if (!outputName) {
    outputName = `${purpose}-${randomUUID()}.json`;
  }

  const targetPath = path.join(tmpDir, `${purpose}-${outputName}`);
  await writeFile(targetPath, contentBuffer);

  return {
    filePath: targetPath,
    buffer: contentBuffer,
    sourceName,
    extractedFrom,
  };
}

export async function collectOutputFiles(outputDir, { previewBytes = 20_000 } = {}) {
  const entries = await readdir(outputDir, { withFileTypes: true });
  const files = [];

  for (const entry of entries) {
    if (!entry.isFile()) {
      continue;
    }
    const filePath = path.join(outputDir, entry.name);
    const fileStats = await stat(filePath);
    const fileData = await readFile(filePath);
    const preview = fileData
      .subarray(0, previewBytes)
      .toString("utf8")
      .replaceAll("\u0000", "");
    files.push({
      name: entry.name,
      size: fileStats.size,
      preview,
      truncated: fileStats.size > previewBytes,
    });
  }

  files.sort((a, b) => a.name.localeCompare(b.name));
  return files;
}
