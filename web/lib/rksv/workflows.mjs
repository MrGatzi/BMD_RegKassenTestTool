import path from "node:path";
import { mkdir, readFile, stat, writeFile } from "node:fs/promises";
import AdmZip from "adm-zip";
import {
  collectOutputFiles,
  createTempWorkspace,
  removeDirSafe,
} from "./files.mjs";
import {
  extractReceiptsFromDep,
  normalizeReceiptInput,
  parseDepExportText,
  splitDepByGroups,
} from "./parsers.mjs";
import { resolveDemoCacheDir, resolveVerifierJars, DEMO_ZIP_URL } from "./paths.mjs";
import { didVerificationPass, runJavaJar, runProcess } from "./process.mjs";

const SCENARIO_NAME = "TESTSUITE_TEST_SZENARIO_1";

async function fileExists(filePath) {
  try {
    await stat(filePath);
    return true;
  } catch {
    return false;
  }
}

async function ensureDir(dirPath) {
  await mkdir(dirPath, { recursive: true });
}

export async function runDepVerification({
  depFilePath,
  cryptoFilePath,
  allowFuture = false,
  verbose = false,
  heapMb = 1500,
  outputDir,
  timeoutMs = 120_000,
  includeOutputFiles = true,
}) {
  const { depJarPath } = await resolveVerifierJars();
  await ensureDir(outputDir);

  const jarArgs = [
    ...(allowFuture ? ["-f"] : []),
    ...(verbose ? ["-v"] : []),
    "-i",
    depFilePath,
    "-c",
    cryptoFilePath,
    "-o",
    outputDir,
  ];

  const processResult = await runJavaJar({
    jarPath: depJarPath,
    jarArgs,
    heapMb,
    timeoutMs,
  });

  const outputFiles = includeOutputFiles
    ? await collectOutputFiles(outputDir, { previewBytes: 20_000 })
    : [];

  return {
    verifier: "depformat",
    passed: didVerificationPass(processResult),
    process: processResult,
    outputFiles,
  };
}

export async function runReceiptVerification({
  receiptsFilePath,
  cryptoFilePath,
  allowFuture = false,
  verbose = false,
  heapMb = 1500,
  outputDir,
  timeoutMs = 120_000,
  includeOutputFiles = true,
}) {
  const { receiptsJarPath } = await resolveVerifierJars();
  await ensureDir(outputDir);

  const jarArgs = [
    ...(allowFuture ? ["-f"] : []),
    ...(verbose ? ["-v"] : []),
    "-i",
    receiptsFilePath,
    "-c",
    cryptoFilePath,
    "-o",
    outputDir,
  ];

  const processResult = await runJavaJar({
    jarPath: receiptsJarPath,
    jarArgs,
    heapMb,
    timeoutMs,
  });

  const outputFiles = includeOutputFiles
    ? await collectOutputFiles(outputDir, { previewBytes: 20_000 })
    : [];

  return {
    verifier: "receipts",
    passed: didVerificationPass(processResult),
    process: processResult,
    outputFiles,
  };
}

export async function runAdvancedSplitWorkflow({
  depFilePath,
  cryptoFilePath,
  runDepTests = true,
  allowFuture = false,
  verbose = false,
  heapMb = 1500,
  timeoutMs = 120_000,
  includeOutputFiles = false,
}) {
  const depText = await readFile(depFilePath, "utf8");
  const depExport = parseDepExportText(depText);
  const segments = splitDepByGroups(depExport);

  const workspace = await createTempWorkspace("rksv-advanced-");

  try {
    const segmentResults = [];
    for (const segment of segments) {
      const segmentDepPath = path.join(
        workspace,
        `segment-${segment.index + 1}.dep-export.json`,
      );
      await writeFile(segmentDepPath, JSON.stringify(segment.depExport, null, 2), "utf8");

      let verification = null;
      if (runDepTests) {
        const segmentOutputDir = path.join(workspace, `segment-${segment.index + 1}-results`);
        verification = await runDepVerification({
          depFilePath: segmentDepPath,
          cryptoFilePath,
          allowFuture,
          verbose,
          heapMb,
          timeoutMs,
          outputDir: segmentOutputDir,
          includeOutputFiles,
        });
      }

      segmentResults.push({
        index: segment.index,
        receiptCount: segment.receiptCount,
        verification,
      });
    }

    const allSegmentsPassed = segmentResults.every(
      (segment) => !segment.verification || segment.verification.passed,
    );

    return {
      segmentCount: segments.length,
      totalReceipts: extractReceiptsFromDep(depExport).length,
      runDepTests,
      allSegmentsPassed,
      segments: segmentResults,
    };
  } finally {
    await removeDirSafe(workspace);
  }
}

async function ensureDemoToolExtracted(cacheDir) {
  const demoDir = path.join(cacheDir, "regkassen-demo-1.0.0");
  const demoJar = path.join(demoDir, "regkassen-demo-1.0.0.jar");
  if (await fileExists(demoJar)) {
    return { demoDir, demoJar };
  }

  await ensureDir(cacheDir);

  const response = await fetch(DEMO_ZIP_URL);
  if (!response.ok) {
    throw new Error(
      `Failed to download demo generator from ${DEMO_ZIP_URL} (${response.status}).`,
    );
  }

  const zipBuffer = Buffer.from(await response.arrayBuffer());
  const zip = new AdmZip(zipBuffer);
  zip.extractAllTo(cacheDir, true);

  if (!(await fileExists(demoJar))) {
    throw new Error(`Demo jar not found after extracting archive into ${cacheDir}.`);
  }

  return { demoDir, demoJar };
}

export async function generateOfficialDemoData({
  closedSystem = true,
  turnoverCounterLength = 8,
  timeoutMs = 120_000,
}) {
  const cacheDir = resolveDemoCacheDir();
  const { demoDir, demoJar } = await ensureDemoToolExtracted(cacheDir);
  const workspace = await createTempWorkspace("rksv-demo-data-");
  const outputDir = path.join(workspace, "generated");
  await ensureDir(outputDir);

  const args = [
    "-Djava.awt.headless=true",
    "-jar",
    demoJar,
    "-o",
    outputDir,
    "-l",
    String(turnoverCounterLength),
    ...(closedSystem ? ["-c"] : []),
  ];

  const generationResult = await runProcess("java", args, {
    cwd: demoDir,
    timeoutMs,
  });

  if (generationResult.exitCode !== 0 || generationResult.timedOut) {
    throw new Error(
      `Test-data generation failed.\n${generationResult.stderr || generationResult.stdout}`,
    );
  }

  const scenarioDir = path.join(outputDir, SCENARIO_NAME);
  const depPath = path.join(scenarioDir, "dep-export.json");
  const cryptoPath = path.join(scenarioDir, "cryptographicMaterialContainer.json");
  const qrPath = path.join(scenarioDir, "qr-code-rep.json");

  if (
    !(await fileExists(depPath)) ||
    !(await fileExists(cryptoPath)) ||
    !(await fileExists(qrPath))
  ) {
    throw new Error(
      `Generated scenario ${SCENARIO_NAME} is incomplete. Expected dep-export.json, cryptographicMaterialContainer.json and qr-code-rep.json.`,
    );
  }

  const depExport = parseDepExportText(await readFile(depPath, "utf8"));
  const receiptCount = extractReceiptsFromDep(depExport).length;
  const receiptArray = normalizeReceiptInput(await readFile(qrPath, "utf8"));

  return {
    scenarioName: SCENARIO_NAME,
    workspace,
    outputDir,
    depPath,
    cryptoPath,
    qrPath,
    receiptCount,
    qrReceiptCount: receiptArray.length,
    generation: generationResult,
  };
}

export async function runWorkflowSelfTest({ cleanup = true } = {}) {
  let generated = null;
  try {
    generated = await generateOfficialDemoData({ closedSystem: true });

    const depOutputDir = path.join(generated.workspace, "dep-verify-out");
    const depResult = await runDepVerification({
      depFilePath: generated.depPath,
      cryptoFilePath: generated.cryptoPath,
      outputDir: depOutputDir,
      includeOutputFiles: false,
    });

    const receiptOutputDir = path.join(generated.workspace, "receipts-verify-out");
    const receiptResult = await runReceiptVerification({
      receiptsFilePath: generated.qrPath,
      cryptoFilePath: generated.cryptoPath,
      outputDir: receiptOutputDir,
      includeOutputFiles: false,
    });

    const advancedResult = await runAdvancedSplitWorkflow({
      depFilePath: generated.depPath,
      cryptoFilePath: generated.cryptoPath,
      runDepTests: true,
      includeOutputFiles: false,
    });

    const ok = depResult.passed && receiptResult.passed && advancedResult.allSegmentsPassed;

    return {
      ok,
      generated: {
        scenarioName: generated.scenarioName,
        receiptCount: generated.receiptCount,
        qrReceiptCount: generated.qrReceiptCount,
      },
      depVerification: {
        passed: depResult.passed,
        exitCode: depResult.process.exitCode,
      },
      receiptVerification: {
        passed: receiptResult.passed,
        exitCode: receiptResult.process.exitCode,
      },
      advancedWorkflow: {
        segmentCount: advancedResult.segmentCount,
        allSegmentsPassed: advancedResult.allSegmentsPassed,
      },
    };
  } finally {
    if (cleanup && generated?.workspace) {
      await removeDirSafe(generated.workspace);
    }
  }
}
