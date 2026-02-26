import path from "node:path";
import { mkdir, writeFile } from "node:fs/promises";
import { NextResponse } from "next/server";
import {
  createTempWorkspace,
  materializeUploadedFile,
  removeDirSafe,
} from "@/lib/rksv/files.mjs";
import { normalizeReceiptInput } from "@/lib/rksv/parsers.mjs";
import { runReceiptVerification } from "@/lib/rksv/workflows.mjs";

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
  const workspace = await createTempWorkspace("rksv-receipts-");

  try {
    const formData = await request.formData();
    const receiptFile = formData.get("receiptFile");
    const cryptoFile = formData.get("cryptoFile");

    if (!(receiptFile instanceof File) || !(cryptoFile instanceof File)) {
      return NextResponse.json(
        {
          ok: false,
          error: "Both receipt input file and crypto material file are required.",
        },
        { status: 400 },
      );
    }

    const allowFuture = parseBoolean(formData.get("allowFuture"));
    const verbose = parseBoolean(formData.get("verbose"));
    const heapMb = parseHeap(formData.get("heapMb"));

    const receiptsInput = await materializeUploadedFile(receiptFile, {
      tmpDir: workspace,
      purpose: "receipts",
    });
    const cryptoInput = await materializeUploadedFile(cryptoFile, {
      tmpDir: workspace,
      purpose: "crypto",
    });

    const normalizedReceipts = normalizeReceiptInput(
      receiptsInput.buffer.toString("utf8"),
    );
    const normalizedReceiptFile = path.join(workspace, "normalized-receipts.json");
    await writeFile(normalizedReceiptFile, JSON.stringify(normalizedReceipts, null, 2), "utf8");

    const outputDir = path.join(workspace, "receipts-verifier-output");
    await mkdir(outputDir, { recursive: true });

    const result = await runReceiptVerification({
      receiptsFilePath: normalizedReceiptFile,
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
        receiptFile: receiptsInput.sourceName,
        receiptDecodedFrom: receiptsInput.extractedFrom,
        normalizedReceiptCount: normalizedReceipts.length,
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
