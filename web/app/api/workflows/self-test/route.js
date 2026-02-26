import { NextResponse } from "next/server";
import { runWorkflowSelfTest } from "@/lib/rksv/workflows.mjs";

export const runtime = "nodejs";

export async function POST() {
  try {
    const report = await runWorkflowSelfTest({ cleanup: true });
    return NextResponse.json(report, {
      status: report.ok ? 200 : 500,
    });
  } catch (error) {
    return NextResponse.json(
      {
        ok: false,
        error: error instanceof Error ? error.message : "Unknown error",
      },
      { status: 500 },
    );
  }
}
