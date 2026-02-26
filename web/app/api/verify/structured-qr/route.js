import { NextResponse } from "next/server";
import {
  createTempWorkspace,
  materializeUploadedFile,
  removeDirSafe,
} from "@/lib/rksv/files.mjs";
import { decryptAndStructureQr } from "@/lib/rksv/decrypt.mjs";

export const runtime = "nodejs";

function parseBoolean(value) {
  return value === "true" || value === "1" || value === "on";
}

export async function POST(request) {
  const workspace = await createTempWorkspace("rksv-qr-struct-");
  try {
    const formData = await request.formData();
    const qrFile = formData.get("qrFile");
    const cryptoFile = formData.get("cryptoFile");
    if (!(qrFile instanceof File) || !(cryptoFile instanceof File)) {
      return NextResponse.json(
        { ok: false, error: "Both QR receipt file and crypto material file are required." },
        { status: 400 },
      );
    }
    const firstReceiptNotIncluded = parseBoolean(formData.get("firstReceiptNotIncluded"));
    const qrInput = await materializeUploadedFile(qrFile, { tmpDir: workspace, purpose: "qr" });
    const cryptoInput = await materializeUploadedFile(cryptoFile, { tmpDir: workspace, purpose: "crypto" });
    const result = decryptAndStructureQr(
      qrInput.buffer.toString("utf8"),
      cryptoInput.buffer.toString("utf8"),
      { firstReceiptNotIncluded },
    );
    return NextResponse.json({ ok: true, input: { qrFile: qrInput.sourceName, cryptoFile: cryptoInput.sourceName }, result });
  } catch (error) {
    return NextResponse.json({ ok: false, error: error instanceof Error ? error.message : "Unknown error" }, { status: 500 });
  } finally {
    await removeDirSafe(workspace);
  }
}
