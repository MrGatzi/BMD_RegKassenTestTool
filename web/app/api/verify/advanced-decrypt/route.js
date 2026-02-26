import { NextResponse } from "next/server";
import {
  createTempWorkspace,
  materializeUploadedFile,
  removeDirSafe,
} from "@/lib/rksv/files.mjs";
import { advancedSplitAndDecrypt } from "@/lib/rksv/decrypt.mjs";

export const runtime = "nodejs";

function parseBoolean(value) {
  return value === "true" || value === "1" || value === "on";
}

export async function POST(request) {
  const workspace = await createTempWorkspace("rksv-adv-decrypt-");
  try {
    const formData = await request.formData();
    const depFile = formData.get("depFile");
    const cryptoFile = formData.get("cryptoFile");
    if (!(depFile instanceof File) || !(cryptoFile instanceof File)) {
      return NextResponse.json(
        { ok: false, error: "Both DEP file and crypto material file are required." },
        { status: 400 },
      );
    }
    const firstReceiptNotIncluded = parseBoolean(formData.get("firstReceiptNotIncluded"));
    const depInput = await materializeUploadedFile(depFile, { tmpDir: workspace, purpose: "dep" });
    const cryptoInput = await materializeUploadedFile(cryptoFile, { tmpDir: workspace, purpose: "crypto" });
    const result = advancedSplitAndDecrypt(
      depInput.buffer.toString("utf8"),
      cryptoInput.buffer.toString("utf8"),
      { firstReceiptNotIncluded },
    );
    return NextResponse.json({ ok: true, input: { depFile: depInput.sourceName, cryptoFile: cryptoInput.sourceName }, result });
  } catch (error) {
    return NextResponse.json({ ok: false, error: error instanceof Error ? error.message : "Unknown error" }, { status: 500 });
  } finally {
    await removeDirSafe(workspace);
  }
}
