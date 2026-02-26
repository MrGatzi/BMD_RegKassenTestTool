import { runWorkflowSelfTest } from "../lib/rksv/workflows.mjs";

try {
  const report = await runWorkflowSelfTest({ cleanup: true });
  console.log(JSON.stringify(report, null, 2));
  if (!report.ok) {
    process.exitCode = 1;
  }
} catch (error) {
  console.error(error);
  process.exitCode = 1;
}
