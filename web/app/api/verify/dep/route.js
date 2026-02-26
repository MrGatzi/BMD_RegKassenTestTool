import path from "node:path";
import { mkdir } from "node:fs/promises";
import { NextResponse } from "next/server";
import {
  createTempWorkspace,
  materializeUploadedFile,
  removeDirSafe,
} from "@/lib/rksv/files.mjs";
import { runDepVerification } from "@/lib/rksv/workflows.mjs";

export const runtime = "nodejs";

function parseBoolean(value) {
  return value === "true" || value === "1" || value === "on";
}

function parseHeap(value) {
  const parsed = Number(value);
  if (!Number.isFinite(parsed)) {
    return 1500;
  }
  return Math.max(256, Math.min(8192, Math.floor(parsed)));
}

export async function POST(request) {
  const workspace = await createTempWorkspace("rksv-dep-");

  try {
    const formData = await request.formData();
    const depFile = formData.get("depFile");
    const cryptoFile = formData.get("cryptoFile");

    if (!(depFile instanceof File) || !(cryptoFile instanceof File)) {
      return NextResponse.json(
        {
          ok: false,
          error: "Both DEP file and crypto material file are required.",
        },
        { status: 400 },
      );
    }

    const allowFuture = parseBoolean(formData.get("allowFuture"));
    const verbose = parseBoolean(formData.get("verbose"));
    const heapMb = parseHeap(formData.get("heapMb"));

    const depInput = await materializeUploadedFile(depFile, {
      tmpDir: workspace,
      purpose: "dep",
    });
    const cryptoInput = await materializeUploadedFile(cryptoFile, {
      tmpDir: workspace,
      purpose: "crypto",
    });

    const outputDir = path.join(workspace, "dep-verifier-output");
    await mkdir(outputDir, { recursive: true });

    const result = await runDepVerification({
      depFilePath: depInput.filePath,
      cryptoFilePath: cryptoInput.filePath,
      allowFuture,
      verbose,
      heapMb,
      outputDir,
      includeOutputFiles: true,
    });

    return NextResponse.json({
      ok: true,
      input: {
        depFile: depInput.sourceName,
        depDecodedFrom: depInput.extractedFrom,
        cryptoFile: cryptoInput.sourceName,
        cryptoDecodedFrom: cryptoInput.extractedFrom,
      },
      result,
    });
  } catch (error) {
    return NextResponse.json(
      {
        ok: false,
        error: error instanceof Error ? error.message : "Unknown error",
      },
      { status: 500 },
    );
  } finally {
    await removeDirSafe(workspace);
  }
}
