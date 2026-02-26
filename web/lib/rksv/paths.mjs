import path from "node:path";
import { access } from "node:fs/promises";
import { constants as fsConstants } from "node:fs";
import { tmpdir } from "node:os";

export const DEP_JAR_FILE = "regkassen-verification-depformat-1.1.1.jar";
export const RECEIPTS_JAR_FILE = "regkassen-verification-receipts-1.1.1.jar";
export const DEMO_ZIP_URL =
  "https://github.com/BMF-RKSV-Technik/at-registrierkassen-mustercode/releases/download/V1.0.0/regkassen-demo-1.0.0.zip";

const DEFAULT_DEMO_DIR_NAME = "regkassen-demo-1.0.0";

async function fileExists(filePath) {
  try {
    await access(filePath, fsConstants.R_OK);
    return true;
  } catch {
    return false;
  }
}

function candidateRoots() {
  return [process.cwd(), path.resolve(process.cwd(), "..")];
}

function resolveMaybeRelative(candidatePath) {
  return path.isAbsolute(candidatePath)
    ? candidatePath
    : path.resolve(process.cwd(), candidatePath);
}

export async function resolveBinaryPath({
  envVarName,
  defaultFileName,
  customErrorLabel,
}) {
  const envValue = process.env[envVarName];
  if (envValue) {
    const envResolved = resolveMaybeRelative(envValue);
    if (await fileExists(envResolved)) {
      return envResolved;
    }

    throw new Error(
      `${customErrorLabel ?? defaultFileName} not found at ${envResolved} (from ${envVarName}).`,
    );
  }

  for (const root of candidateRoots()) {
    const fromRoot = path.join(root, defaultFileName);
    if (await fileExists(fromRoot)) {
      return fromRoot;
    }
  }

  throw new Error(
    `${customErrorLabel ?? defaultFileName} not found. Checked ${candidateRoots()
      .map((root) => path.join(root, defaultFileName))
      .join(", ")}. Set ${envVarName} to override.`,
  );
}

export async function resolveVerifierJars() {
  const depJarPath = await resolveBinaryPath({
    envVarName: "RKSV_DEP_JAR_PATH",
    defaultFileName: DEP_JAR_FILE,
    customErrorLabel: "DEP verifier jar",
  });
  const receiptsJarPath = await resolveBinaryPath({
    envVarName: "RKSV_RECEIPTS_JAR_PATH",
    defaultFileName: RECEIPTS_JAR_FILE,
    customErrorLabel: "Receipt verifier jar",
  });

  return { depJarPath, receiptsJarPath };
}

export function defaultCacheDir() {
  return path.join(tmpdir(), "rksv-web-cache");
}

export function resolveDemoCacheDir() {
  const configuredDir = process.env.RKSV_DEMO_CACHE_DIR;
  if (!configuredDir) {
    return defaultCacheDir();
  }

  return resolveMaybeRelative(configuredDir);
}

export function demoExtractedDirectory(cacheDir) {
  return path.join(cacheDir, DEFAULT_DEMO_DIR_NAME);
}
