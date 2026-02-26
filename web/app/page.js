"use client";

import { useState } from "react";

const TABS = [
  { id: "dep", label: "DEP export verification" },
  { id: "receipts", label: "Single receipts verification" },
  { id: "advanced", label: "Advanced split + verify" },
  { id: "selftest", label: "Workflow self-test" },
];

function clsx(...parts) {
  return parts.filter(Boolean).join(" ");
}

function ResultPanel({ result }) {
  if (!result) {
    return (
      <div className="rounded-xl border border-dashed border-slate-300 bg-white/70 p-4 text-sm text-slate-600">
        No result yet.
      </div>
    );
  }

  const success = result.ok ?? result.result?.passed ?? false;
  return (
    <div className="space-y-2">
      <div
        className={clsx(
          "inline-flex rounded-full px-3 py-1 text-xs font-semibold",
          success
            ? "bg-emerald-100 text-emerald-800"
            : "bg-rose-100 text-rose-700",
        )}
      >
        {success ? "Success" : "Failed"}
      </div>
      <pre className="max-h-[30rem] overflow-auto rounded-xl border border-slate-200 bg-slate-950 p-4 text-xs text-slate-100">
        {JSON.stringify(result, null, 2)}
      </pre>
    </div>
  );
}

export default function Home() {
  const [activeTab, setActiveTab] = useState("dep");
  const [isBusy, setIsBusy] = useState({
    dep: false,
    receipts: false,
    advanced: false,
    selftest: false,
  });
  const [results, setResults] = useState({
    dep: null,
    receipts: null,
    advanced: null,
    selftest: null,
  });

  async function submitForm(tab, event, endpoint) {
    event.preventDefault();
    setIsBusy((prev) => ({ ...prev, [tab]: true }));
    try {
      const formData = new FormData(event.currentTarget);
      const response = await fetch(endpoint, {
        method: "POST",
        body: formData,
      });
      const payload = await response.json();
      setResults((prev) => ({ ...prev, [tab]: payload }));
    } catch (error) {
      setResults((prev) => ({
        ...prev,
        [tab]: { ok: false, error: String(error) },
      }));
    } finally {
      setIsBusy((prev) => ({ ...prev, [tab]: false }));
    }
  }

  async function runSelfTest() {
    setIsBusy((prev) => ({ ...prev, selftest: true }));
    try {
      const response = await fetch("/api/workflows/self-test", { method: "POST" });
      const payload = await response.json();
      setResults((prev) => ({ ...prev, selftest: payload }));
    } catch (error) {
      setResults((prev) => ({
        ...prev,
        selftest: { ok: false, error: String(error) },
      }));
    } finally {
      setIsBusy((prev) => ({ ...prev, selftest: false }));
    }
  }

  return (
    <main className="min-h-screen bg-gradient-to-b from-slate-50 via-slate-100 to-slate-200 p-6 text-slate-900 md:p-10">
      <div className="mx-auto max-w-7xl space-y-6">
        <header className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
          <h1 className="text-3xl font-bold tracking-tight">
            RKSV Verification Web Tool
          </h1>
          <p className="mt-2 max-w-4xl text-sm text-slate-600">
            Modern Next.js wrapper around the official RKSV verifier jars. Upload
            plain JSON, GZIP or ZIP files and run DEP/receipt verification with a
            cleaner web workflow.
          </p>
        </header>

        <div className="grid gap-6 lg:grid-cols-[2fr_1fr]">
          <section className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
            <div className="mb-4 flex flex-wrap gap-2">
              {TABS.map((tab) => (
                <button
                  key={tab.id}
                  type="button"
                  onClick={() => setActiveTab(tab.id)}
                  className={clsx(
                    "rounded-full px-4 py-2 text-sm font-medium transition",
                    activeTab === tab.id
                      ? "bg-slate-900 text-white"
                      : "bg-slate-100 text-slate-700 hover:bg-slate-200",
                  )}
                >
                  {tab.label}
                </button>
              ))}
            </div>

            {activeTab === "dep" && (
              <form
                className="space-y-4"
                onSubmit={(event) => submitForm("dep", event, "/api/verify/dep")}
              >
                <div className="grid gap-4 md:grid-cols-2">
                  <label className="space-y-1 text-sm">
                    <span className="font-medium">DEP export file</span>
                    <input
                      required
                      name="depFile"
                      type="file"
                      className="w-full rounded-lg border border-slate-300 px-3 py-2"
                    />
                  </label>
                  <label className="space-y-1 text-sm">
                    <span className="font-medium">Crypto material file</span>
                    <input
                      required
                      name="cryptoFile"
                      type="file"
                      className="w-full rounded-lg border border-slate-300 px-3 py-2"
                    />
                  </label>
                </div>
                <div className="flex flex-wrap items-center gap-4 text-sm">
                  <label className="inline-flex items-center gap-2">
                    <input name="allowFuture" type="checkbox" />
                    Allow future timestamps
                  </label>
                  <label className="inline-flex items-center gap-2">
                    <input name="verbose" type="checkbox" />
                    Verbose verifier output
                  </label>
                  <label className="inline-flex items-center gap-2">
                    Heap MB
                    <input
                      name="heapMb"
                      type="number"
                      min={256}
                      max={8192}
                      defaultValue={1500}
                      className="w-24 rounded border border-slate-300 px-2 py-1"
                    />
                  </label>
                </div>
                <button
                  disabled={isBusy.dep}
                  className="rounded-lg bg-slate-900 px-4 py-2 text-sm font-semibold text-white disabled:opacity-50"
                  type="submit"
                >
                  {isBusy.dep ? "Running..." : "Run DEP verification"}
                </button>
                <ResultPanel result={results.dep} />
              </form>
            )}

            {activeTab === "receipts" && (
              <form
                className="space-y-4"
                onSubmit={(event) =>
                  submitForm("receipts", event, "/api/verify/receipts")
                }
              >
                <div className="grid gap-4 md:grid-cols-2">
                  <label className="space-y-1 text-sm">
                    <span className="font-medium">
                      Receipts input (qr-code-rep.json, DEP, or lines)
                    </span>
                    <input
                      required
                      name="receiptFile"
                      type="file"
                      className="w-full rounded-lg border border-slate-300 px-3 py-2"
                    />
                  </label>
                  <label className="space-y-1 text-sm">
                    <span className="font-medium">Crypto material file</span>
                    <input
                      required
                      name="cryptoFile"
                      type="file"
                      className="w-full rounded-lg border border-slate-300 px-3 py-2"
                    />
                  </label>
                </div>
                <div className="flex flex-wrap items-center gap-4 text-sm">
                  <label className="inline-flex items-center gap-2">
                    <input name="allowFuture" type="checkbox" />
                    Allow future timestamps
                  </label>
                  <label className="inline-flex items-center gap-2">
                    <input name="verbose" type="checkbox" />
                    Verbose verifier output
                  </label>
                  <label className="inline-flex items-center gap-2">
                    Heap MB
                    <input
                      name="heapMb"
                      type="number"
                      min={256}
                      max={8192}
                      defaultValue={1500}
                      className="w-24 rounded border border-slate-300 px-2 py-1"
                    />
                  </label>
                </div>
                <button
                  disabled={isBusy.receipts}
                  className="rounded-lg bg-slate-900 px-4 py-2 text-sm font-semibold text-white disabled:opacity-50"
                  type="submit"
                >
                  {isBusy.receipts ? "Running..." : "Run receipt verification"}
                </button>
                <ResultPanel result={results.receipts} />
              </form>
            )}

            {activeTab === "advanced" && (
              <form
                className="space-y-4"
                onSubmit={(event) =>
                  submitForm("advanced", event, "/api/verify/advanced")
                }
              >
                <div className="grid gap-4 md:grid-cols-2">
                  <label className="space-y-1 text-sm">
                    <span className="font-medium">DEP export file</span>
                    <input
                      required
                      name="depFile"
                      type="file"
                      className="w-full rounded-lg border border-slate-300 px-3 py-2"
                    />
                  </label>
                  <label className="space-y-1 text-sm">
                    <span className="font-medium">Crypto material file</span>
                    <input
                      required
                      name="cryptoFile"
                      type="file"
                      className="w-full rounded-lg border border-slate-300 px-3 py-2"
                    />
                  </label>
                </div>
                <div className="flex flex-wrap items-center gap-4 text-sm">
                  <label className="inline-flex items-center gap-2">
                    <input defaultChecked name="runDepTests" type="checkbox" />
                    Run DEP verification for each split
                  </label>
                  <label className="inline-flex items-center gap-2">
                    <input name="allowFuture" type="checkbox" />
                    Allow future timestamps
                  </label>
                  <label className="inline-flex items-center gap-2">
                    <input name="verbose" type="checkbox" />
                    Verbose verifier output
                  </label>
                  <label className="inline-flex items-center gap-2">
                    Heap MB
                    <input
                      name="heapMb"
                      type="number"
                      min={256}
                      max={8192}
                      defaultValue={1500}
                      className="w-24 rounded border border-slate-300 px-2 py-1"
                    />
                  </label>
                </div>
                <button
                  disabled={isBusy.advanced}
                  className="rounded-lg bg-slate-900 px-4 py-2 text-sm font-semibold text-white disabled:opacity-50"
                  type="submit"
                >
                  {isBusy.advanced ? "Running..." : "Run advanced split workflow"}
                </button>
                <ResultPanel result={results.advanced} />
              </form>
            )}

            {activeTab === "selftest" && (
              <div className="space-y-4">
                <p className="text-sm text-slate-600">
                  Runs the official demo generator in headless mode and verifies
                  all workflows automatically (DEP, receipts, and advanced split).
                </p>
                <button
                  disabled={isBusy.selftest}
                  onClick={runSelfTest}
                  className="rounded-lg bg-indigo-600 px-4 py-2 text-sm font-semibold text-white disabled:opacity-50"
                  type="button"
                >
                  {isBusy.selftest ? "Running self-test..." : "Run workflow self-test"}
                </button>
                <ResultPanel result={results.selftest} />
              </div>
            )}
          </section>

          <aside className="space-y-4">
            <section className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
              <h2 className="text-base font-semibold">Accepted input</h2>
              <ul className="mt-2 list-disc space-y-1 pl-5 text-sm text-slate-600">
                <li>.json files directly</li>
                <li>.gz compressed JSON</li>
                <li>.zip archives (best-matching JSON is auto-selected)</li>
              </ul>
            </section>
            <section className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
              <h2 className="text-base font-semibold">Official documentation</h2>
              <ul className="mt-2 space-y-2 text-sm">
                <li>
                  <a
                    className="text-indigo-700 underline"
                    href="https://github.com/BMF-RKSV-Technik/at-registrierkassen-mustercode"
                    target="_blank"
                    rel="noreferrer"
                  >
                    BMF RKSV mustercode repository
                  </a>
                </li>
                <li>
                  <a
                    className="text-indigo-700 underline"
                    href="https://github.com/BMF-RKSV-Technik/at-registrierkassen-mustercode/releases/tag/V1.1.1"
                    target="_blank"
                    rel="noreferrer"
                  >
                    Prüftool 1.1.1 release
                  </a>
                </li>
                <li>
                  <a
                    className="text-indigo-700 underline"
                    href="https://github.com/BMF-RKSV-Technik/at-registrierkassen-mustercode/releases/tag/V1.0.0"
                    target="_blank"
                    rel="noreferrer"
                  >
                    Demo/test-data release
                  </a>
                </li>
              </ul>
            </section>
            <section className="rounded-2xl border border-slate-200 bg-white p-5 text-sm text-slate-600 shadow-sm">
              <p>
                For Vercel hosting, this UI deploys directly. The API routes need
                a Java runtime and access to verifier jars. If Java is unavailable
                in your serverless runtime, host verification in a separate backend
                service and point the UI there.
              </p>
            </section>
          </aside>
        </div>
      </div>
    </main>
  );
}
