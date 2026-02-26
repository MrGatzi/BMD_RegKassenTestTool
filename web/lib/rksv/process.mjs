import { spawn } from "node:child_process";

const DEFAULT_TIMEOUT_MS = 120_000;
const DEFAULT_MAX_CAPTURE_CHARS = 500_000;

function appendBounded(current, chunk, limit) {
  if (current.length >= limit) {
    return current;
  }

  const remaining = limit - current.length;
  if (chunk.length <= remaining) {
    return current + chunk;
  }

  return `${current}${chunk.slice(0, remaining)}\n...[truncated]`;
}

function quoteArg(value) {
  if (/^[a-zA-Z0-9._\-/:=]+$/.test(value)) {
    return value;
  }
  return `"${String(value).replaceAll(`"`, `\\"`)}"`;
}

export async function runProcess(command, args, options = {}) {
  const {
    cwd,
    env,
    timeoutMs = DEFAULT_TIMEOUT_MS,
    maxCapturedChars = DEFAULT_MAX_CAPTURE_CHARS,
  } = options;

  return await new Promise((resolve, reject) => {
    const startedAt = Date.now();
    const child = spawn(command, args, {
      cwd,
      env: { ...process.env, ...env },
      stdio: ["ignore", "pipe", "pipe"],
    });

    let stdout = "";
    let stderr = "";
    let timedOut = false;

    const timer = setTimeout(() => {
      timedOut = true;
      child.kill("SIGTERM");
      setTimeout(() => child.kill("SIGKILL"), 3_000).unref();
    }, timeoutMs);

    child.stdout.on("data", (chunk) => {
      stdout = appendBounded(stdout, String(chunk), maxCapturedChars);
    });

    child.stderr.on("data", (chunk) => {
      stderr = appendBounded(stderr, String(chunk), maxCapturedChars);
    });

    child.on("error", (error) => {
      clearTimeout(timer);
      reject(error);
    });

    child.on("close", (exitCode, signal) => {
      clearTimeout(timer);
      resolve({
        exitCode,
        signal,
        timedOut,
        durationMs: Date.now() - startedAt,
        stdout,
        stderr,
        commandLine: [command, ...args].map(quoteArg).join(" "),
      });
    });
  });
}

export async function runJavaJar({
  jarPath,
  jarArgs,
  heapMb = 1500,
  cwd,
  timeoutMs,
}) {
  const javaArgs = [`-Xmx${heapMb}m`, "-jar", jarPath, ...jarArgs];
  return await runProcess("java", javaArgs, { cwd, timeoutMs });
}

export function didVerificationPass(processResult) {
  if (processResult.exitCode !== 0 || processResult.timedOut) {
    return false;
  }

  const text = `${processResult.stdout}\n${processResult.stderr}`;
  return !/\bFAIL\b/.test(text);
}
