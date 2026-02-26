"use client";

import { useState, useRef } from "react";
import {
  FileText,
  SearchCheck,
  QrCode,
  SplitSquareHorizontal,
  FlaskConical,
  FolderOpen,
  ChevronUp,
  ChevronDown,
  CircleCheck,
  CircleX,
  Lock,
  Unlock,
  Link2,
  ExternalLink,
  Loader2,
  FileJson,
  Archive,
  FileArchive,
} from "lucide-react";

const TABS = [
  { id: "structured", label: "DEP-Datei", Icon: FileText, desc: "Decrypt & structure" },
  { id: "dep", label: "DEP-Test", Icon: SearchCheck, desc: "Official JAR verification" },
  { id: "receipts", label: "QR-Test", Icon: QrCode, desc: "Single receipt verification" },
  { id: "advanced", label: "Erweitert", Icon: SplitSquareHorizontal, desc: "Advanced split + verify" },
  { id: "selftest", label: "Selbsttest", Icon: FlaskConical, desc: "Workflow self-test" },
];

function cx(...parts) {
  return parts.filter(Boolean).join(" ");
}

function FileInput({ name, label, required = true, accept = ".json,.gz,.zip" }) {
  const ref = useRef(null);
  const [fileName, setFileName] = useState(null);
  return (
    <div className="space-y-1.5">
      <label className="text-xs font-semibold uppercase tracking-wide text-slate-500">{label}</label>
      <button
        type="button"
        onClick={() => ref.current?.click()}
        className="flex w-full items-center gap-3 rounded-xl border-2 border-dashed border-slate-300 bg-slate-50 px-4 py-3 text-left text-sm transition hover:border-indigo-400 hover:bg-indigo-50"
      >
        <FolderOpen className="h-5 w-5 shrink-0 text-slate-400" />
        <span className={fileName ? "text-slate-800 font-medium" : "text-slate-400"}>
          {fileName || "Choose file…"}
        </span>
      </button>
      <input
        ref={ref}
        name={name}
        type="file"
        accept={accept}
        required={required}
        className="hidden"
        onChange={(e) => setFileName(e.target.files?.[0]?.name || null)}
      />
    </div>
  );
}

function Badge({ ok }) {
  return (
    <span className={cx(
      "inline-flex items-center gap-1.5 rounded-full px-3 py-1 text-xs font-bold uppercase tracking-wider",
      ok ? "bg-emerald-100 text-emerald-700" : "bg-rose-100 text-rose-700"
    )}>
      {ok ? <CircleCheck className="h-3.5 w-3.5" /> : <CircleX className="h-3.5 w-3.5" />}
      {ok ? "Erfolgreich" : "Fehler"}
    </span>
  );
}

function SummaryCard({ summary, germanSummary }) {
  if (!summary) return null;
  const allOk = summary.chainErrors.length === 0 &&
    summary.revenueErrors.length === 0 &&
    summary.amountFormatErrors.length === 0 &&
    summary.structureErrors.length === 0 &&
    summary.receiptIdErrors.length === 0 &&
    summary.dateFormatErrors.length === 0 &&
    summary.dateChainErrors.length === 0;
  return (
    <div className={cx(
      "rounded-2xl border-2 p-5",
      allOk ? "border-emerald-200 bg-emerald-50" : "border-rose-200 bg-rose-50"
    )}>
      <div className="mb-3 flex items-center justify-between">
        <h3 className="text-sm font-bold uppercase tracking-wider text-slate-600">Zusammenfassung</h3>
        <Badge ok={allOk} />
      </div>
      <pre className="whitespace-pre-wrap font-mono text-sm leading-relaxed text-slate-700">{germanSummary}</pre>
    </div>
  );
}

function ReceiptCard({ r, defaultOpen = false }) {
  const [open, setOpen] = useState(defaultOpen);
  const hasError = !r.chainOk || !r.revenueOk || !r.dateOk || !r.dateChainOk;
  return (
    <div className={cx(
      "rounded-xl border transition",
      hasError ? "border-rose-300 bg-rose-50/50" : "border-slate-200 bg-white",
    )}>
      <button
        type="button"
        onClick={() => setOpen((v) => !v)}
        className="flex w-full items-center justify-between px-4 py-3 text-left text-sm"
      >
        <div className="flex items-center gap-3">
          <span className={cx(
            "flex h-7 w-7 items-center justify-center rounded-lg text-xs font-bold",
            hasError ? "bg-rose-200 text-rose-700" : "bg-slate-100 text-slate-600"
          )}>
            {r.index}
          </span>
          <span className="font-semibold text-slate-800">{r.receiptId}</span>
          <span className="text-xs text-slate-400">{r.receiptDate}</span>
        </div>
        <div className="flex items-center gap-2">
          {!r.chainOk && <span className="inline-flex items-center gap-1 rounded bg-rose-200 px-1.5 py-0.5 text-[10px] font-bold text-rose-700"><Link2 className="h-3 w-3" />KETTE</span>}
          {!r.revenueOk && <span className="inline-flex items-center gap-1 rounded bg-rose-200 px-1.5 py-0.5 text-[10px] font-bold text-rose-700"><Lock className="h-3 w-3" />UMSATZ</span>}
          {open ? <ChevronUp className="h-4 w-4 text-slate-400" /> : <ChevronDown className="h-4 w-4 text-slate-400" />}
        </div>
      </button>
      {open && (
        <div className="border-t border-slate-100 px-4 pb-4 pt-3">
          <div className="grid gap-x-8 gap-y-2 text-xs md:grid-cols-2">
            <Row label="ZDA" value={r.zda} />
            <Row label="Kassen-ID" value={r.registerId} />
            <Row label="Belegnummer" value={r.receiptId} />
            <Row label="Datum/Uhrzeit" value={r.receiptDate} ok={r.dateOk && r.dateChainOk} />
            <div className="col-span-full my-1 border-t border-dashed border-slate-200" />
            <Row label="Betrag Normal" value={r.setNormal} />
            <Row label="Betrag Ermäßigt-1" value={r.setReduced1} />
            <Row label="Betrag Ermäßigt-2" value={r.setReduced2} />
            <Row label="Betrag Null" value={r.setNull} />
            <Row label="Betrag Besonders" value={r.setSpecial} />
            <div className="col-span-full my-1 border-t border-dashed border-slate-200" />
            <Row label="Umsatz verschlüsselt (AES-256-ICM)" value={r.revenueEncrypted} mono />
            <Row label="Umsatz entschlüsselt" value={r.revenueDecrypted} ok={r.revenueOk} highlight />
            {r.revenueShouldBe !== null && (
              <Row label="Umsatz Sollsumme" value={r.revenueShouldBe} ok={r.revenueOk} highlight />
            )}
            <div className="col-span-full my-1 border-t border-dashed border-slate-200" />
            <Row label="Zertifikat-Seriennummer" value={r.certificateSerial} mono />
            <Row label="Sig Voriger Beleg" value={r.chainValuePrevious} mono />
            <Row label="Sig Voriger (berechnet)" value={r.chainValuePreviousCalc} ok={r.chainOk} mono />
            <Row label="Sig Nächster (berechnet)" value={r.chainValueNext} mono />
            <Row label="Signatur" value={r.signature} mono className="col-span-full" />
          </div>
        </div>
      )}
    </div>
  );
}

function Row({ label, value, ok, mono, highlight, className }) {
  return (
    <div className={cx("flex items-start gap-2", className)}>
      <span className="min-w-[180px] shrink-0 font-medium text-slate-500">{label}</span>
      <span className={cx(
        "break-all",
        mono && "font-mono",
        highlight && "font-semibold",
        ok === false && "text-rose-600 font-bold",
        ok === true && "text-emerald-700",
        ok === undefined && "text-slate-800"
      )}>
        {value ?? "—"}
      </span>
    </div>
  );
}

function StreamPanel({ lines, done, exitCode, durationMs }) {
  const hasFail = lines.some((l) => /\bFAIL\b/.test(l));
  const passed = done && exitCode === 0 && !hasFail;
  return (
    <div className="space-y-3">
      {done && <Badge ok={passed} />}
      {done && (
        <p className="text-xs text-slate-500">
          Exit code: {exitCode} — {(durationMs / 1000).toFixed(1)}s
        </p>
      )}
      <div className="relative max-h-[40rem] overflow-auto rounded-xl border border-slate-200 bg-slate-950 p-4 font-mono text-xs leading-relaxed text-slate-100">
        {lines.length === 0 && !done && (
          <span className="flex items-center gap-2 text-slate-500">
            <Loader2 className="h-3 w-3 animate-spin" /> Warte auf Backend…
          </span>
        )}
        {lines.map((line, i) => (
          <div key={i} className={/\bPASS\b/.test(line) ? "text-emerald-400" : /\bFAIL\b/.test(line) ? "text-rose-400 font-bold" : ""}>
            {line}
          </div>
        ))}
      </div>
    </div>
  );
}

function ResultPanel({ result, tab, stream }) {
  if (stream && (stream.lines.length > 0 || !stream.done)) {
    return <StreamPanel {...stream} />;
  }

  if (!result) {
    return (
      <div className="flex h-48 items-center justify-center rounded-2xl border-2 border-dashed border-slate-200 text-sm text-slate-400">
        Noch kein Ergebnis.
      </div>
    );
  }

  if (tab === "structured" && result.ok && result.result) {
    const { receipts, summary, germanSummary } = result.result;
    return (
      <div className="space-y-4">
        <SummaryCard summary={summary} germanSummary={germanSummary} />
        <div className="space-y-2">
          <h3 className="text-xs font-bold uppercase tracking-wider text-slate-500">
            Belege ({receipts.length})
          </h3>
          {receipts.map((r) => (
            <ReceiptCard key={r.index} r={r} defaultOpen={r.index === 0} />
          ))}
        </div>
      </div>
    );
  }

  const success = result.ok ?? result.result?.passed ?? false;
  return (
    <div className="space-y-3">
      <Badge ok={success} />
      <pre className="max-h-[40rem] overflow-auto rounded-xl border border-slate-200 bg-slate-950 p-4 font-mono text-xs leading-relaxed text-slate-100">
        {JSON.stringify(result, null, 2)}
      </pre>
    </div>
  );
}

const BACKEND_URL = process.env.NEXT_PUBLIC_RKSV_BACKEND_URL || "";

export default function Home() {
  const [activeTab, setActiveTab] = useState("structured");
  const [isBusy, setIsBusy] = useState({});
  const [results, setResults] = useState({});
  const [streams, setStreams] = useState({});

  async function submitForm(tab, event, endpoint) {
    event.preventDefault();
    setIsBusy((prev) => ({ ...prev, [tab]: true }));
    try {
      const formData = new FormData(event.currentTarget);
      const response = await fetch(endpoint, { method: "POST", body: formData });
      const payload = await response.json();
      setResults((prev) => ({ ...prev, [tab]: payload }));
    } catch (error) {
      setResults((prev) => ({ ...prev, [tab]: { ok: false, error: String(error) } }));
    } finally {
      setIsBusy((prev) => ({ ...prev, [tab]: false }));
    }
  }

  async function submitStreaming(tab, event, backendType) {
    event.preventDefault();
    const backendUrl = BACKEND_URL;
    if (!backendUrl) {
      return submitForm(tab, event, `/api/verify/${backendType}`);
    }

    setIsBusy((prev) => ({ ...prev, [tab]: true }));
    setStreams((prev) => ({ ...prev, [tab]: { lines: [], done: false, exitCode: null, durationMs: 0 } }));
    setResults((prev) => ({ ...prev, [tab]: null }));

    try {
      const form = new FormData(event.currentTarget);
      const body = new FormData();
      body.set("inputFile", form.get("depFile") || form.get("receiptFile"));
      body.set("cryptoFile", form.get("cryptoFile"));
      body.set("allowFuture", form.get("allowFuture") ? "true" : "false");
      body.set("verbose", form.get("verbose") ? "true" : "false");
      body.set("heapMb", form.get("heapMb") || "1500");

      const res = await fetch(`${backendUrl}/verify/${backendType}`, { method: "POST", body });
      const reader = res.body.getReader();
      const decoder = new TextDecoder();
      let buf = "";

      while (true) {
        const { done, value } = await reader.read();
        if (done) break;
        buf += decoder.decode(value, { stream: true });
        const parts = buf.split("\n\n");
        buf = parts.pop() || "";
        for (const part of parts) {
          const eventMatch = part.match(/^event: (\w+)\ndata: (.+)$/s);
          if (!eventMatch) continue;
          const [, evtType, evtData] = eventMatch;
          const data = JSON.parse(evtData);

          if (evtType === "stdout" || evtType === "stderr") {
            const newLines = data.text.split("\n").filter((l) => l.trim());
            setStreams((prev) => ({
              ...prev,
              [tab]: { ...prev[tab], lines: [...prev[tab].lines, ...newLines] },
            }));
          } else if (evtType === "done") {
            setStreams((prev) => ({
              ...prev,
              [tab]: { ...prev[tab], done: true, exitCode: data.exitCode, durationMs: data.durationMs },
            }));
          } else if (evtType === "error") {
            setStreams((prev) => ({
              ...prev,
              [tab]: { ...prev[tab], done: true, lines: [...prev[tab].lines, `ERROR: ${data.message}`] },
            }));
          }
        }
      }
    } catch (error) {
      setStreams((prev) => ({
        ...prev,
        [tab]: { lines: [`Connection error: ${error}`], done: true, exitCode: -1, durationMs: 0 },
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
      setResults((prev) => ({ ...prev, selftest: { ok: false, error: String(error) } }));
    } finally {
      setIsBusy((prev) => ({ ...prev, selftest: false }));
    }
  }

  const tab = TABS.find((t) => t.id === activeTab);

  return (
    <div className="flex h-screen bg-slate-100">
      {/* Sidebar */}
      <aside className="flex w-20 flex-col items-center border-r border-slate-200 bg-white py-6">
        <div className="mb-8 flex h-12 w-12 items-center justify-center rounded-2xl bg-slate-900 shadow-lg shadow-slate-900/30">
          <svg viewBox="0 0 32 32" className="h-7 w-7" fill="none">
            <rect x="4" y="4" width="10" height="10" rx="2" fill="#f97316" />
            <rect x="18" y="4" width="10" height="10" rx="2" fill="#fb923c" opacity="0.7" />
            <rect x="4" y="18" width="10" height="10" rx="2" fill="#fb923c" opacity="0.7" />
            <rect x="18" y="18" width="10" height="10" rx="2" fill="#f97316" />
          </svg>
        </div>
        <nav className="flex flex-1 flex-col items-center gap-1">
          {TABS.map((t) => {
            const Icon = t.Icon;
            return (
              <button
                key={t.id}
                type="button"
                onClick={() => setActiveTab(t.id)}
                title={t.label}
                className={cx(
                  "flex h-14 w-14 flex-col items-center justify-center rounded-xl text-xs transition",
                  activeTab === t.id
                    ? "bg-slate-900 text-white shadow-md"
                    : "text-slate-500 hover:bg-slate-100 hover:text-slate-800"
                )}
              >
                <Icon className="h-5 w-5" />
                <span className="mt-1 text-[9px] font-semibold leading-tight">{t.label}</span>
              </button>
            );
          })}
        </nav>
      </aside>

      {/* Main content */}
      <main className="flex flex-1 flex-col overflow-hidden">
        {/* Header */}
        <header className="flex items-center justify-between border-b border-slate-200 bg-white px-8 py-4">
          <div>
            <h1 className="text-xl font-bold tracking-tight text-slate-900">
              BMD RegKassenTestTool
            </h1>
            <p className="text-xs text-slate-500">
              {tab?.label} — {tab?.desc}
            </p>
          </div>
          <div className="flex items-center gap-3">
            <a
              href="https://github.com/BMF-RKSV-Technik/at-registrierkassen-mustercode"
              target="_blank"
              rel="noreferrer"
              className="rounded-lg bg-slate-100 px-3 py-1.5 text-xs font-medium text-slate-600 transition hover:bg-slate-200"
            >
              <span className="flex items-center gap-1.5">BMF RKSV Docs <ExternalLink className="h-3 w-3" /></span>
            </a>
          </div>
        </header>

        {/* Split: config + output */}
        <div className="flex flex-1 overflow-hidden">
          {/* Config panel (left) */}
          <section className="w-[420px] shrink-0 overflow-y-auto border-r border-slate-200 bg-white p-6">
            {activeTab === "structured" && (
              <form className="space-y-5" onSubmit={(e) => submitForm("structured", e, "/api/verify/structured")}>
                <FileInput name="depFile" label="DEP-Export Datei" />
                <FileInput name="cryptoFile" label="Kryptografisches Material" />
                <label className="flex items-center gap-2 text-sm text-slate-600">
                  <input name="firstReceiptNotIncluded" type="checkbox" className="rounded" />
                  Startbeleg nicht enthalten
                </label>
                <SubmitButton busy={isBusy.structured} label="DEP-Datei entschlüsseln" color="indigo" />
              </form>
            )}

            {activeTab === "dep" && (
              <form className="space-y-5" onSubmit={(e) => submitStreaming("dep", e, "dep")}>
                <FileInput name="depFile" label="DEP-Export Datei" />
                <FileInput name="cryptoFile" label="Kryptografisches Material" />
                <OptionRow>
                  <Check name="allowFuture" label="Zukünftige Daten erlauben" />
                  <Check name="verbose" label="Detaillierte Ausgabe" />
                </OptionRow>
                <HeapInput />
                <SubmitButton busy={isBusy.dep} label="DEP-Test ausführen" />
                {!BACKEND_URL && <BackendHint />}
              </form>
            )}

            {activeTab === "receipts" && (
              <form className="space-y-5" onSubmit={(e) => submitStreaming("receipts", e, "receipts")}>
                <FileInput name="receiptFile" label="Belege (qr-code-rep.json / DEP)" />
                <FileInput name="cryptoFile" label="Kryptografisches Material" />
                <OptionRow>
                  <Check name="allowFuture" label="Zukünftige Daten erlauben" />
                  <Check name="verbose" label="Detaillierte Ausgabe" />
                </OptionRow>
                <HeapInput />
                <SubmitButton busy={isBusy.receipts} label="QR-Test ausführen" />
                {!BACKEND_URL && <BackendHint />}
              </form>
            )}

            {activeTab === "advanced" && (
              <form className="space-y-5" onSubmit={(e) => submitForm("advanced", e, "/api/verify/advanced")}>
                <FileInput name="depFile" label="DEP-Export Datei" />
                <FileInput name="cryptoFile" label="Kryptografisches Material" />
                <OptionRow>
                  <Check name="runDepTests" label="DEP-Test je Segment" defaultChecked />
                  <Check name="allowFuture" label="Zukünftige Daten" />
                  <Check name="verbose" label="Details" />
                </OptionRow>
                <HeapInput />
                <SubmitButton busy={isBusy.advanced} label="Erweiterten Test ausführen" />
              </form>
            )}


            {activeTab === "selftest" && (
              <div className="space-y-5">
                <p className="text-sm leading-relaxed text-slate-600">
                  Führt den offiziellen Demo-Generator im Headless-Modus aus und verifiziert
                  alle Workflows automatisch (DEP, Belege, Erweitert).
                </p>
                <SubmitButton busy={isBusy.selftest} label="Selbsttest starten" color="violet" onClick={runSelfTest} />
              </div>
            )}

            {/* Info cards */}
            <div className="mt-8 space-y-4">
              <InfoCard title="Akzeptierte Eingabe">
                <ul className="space-y-2 text-xs text-slate-500">
                  <li className="flex items-center gap-2"><FileJson className="h-3.5 w-3.5 text-slate-400" /> .json Dateien direkt</li>
                  <li className="flex items-center gap-2"><Archive className="h-3.5 w-3.5 text-slate-400" /> .gz komprimiertes JSON</li>
                  <li className="flex items-center gap-2"><FileArchive className="h-3.5 w-3.5 text-slate-400" /> .zip Archive (automatische Auswahl)</li>
                </ul>
              </InfoCard>
            </div>
          </section>

          {/* Results panel (right) */}
          <section className="flex-1 overflow-y-auto bg-slate-50 p-6">
            <ResultPanel result={results[activeTab]} tab={activeTab} stream={streams[activeTab]} />
          </section>
        </div>
      </main>
    </div>
  );
}

function SubmitButton({ busy, label, color = "slate", onClick }) {
  const colors = {
    slate: "bg-slate-900 hover:bg-slate-800",
    indigo: "bg-indigo-600 hover:bg-indigo-700",
    violet: "bg-violet-600 hover:bg-violet-700",
  };
  return (
    <button
      type={onClick ? "button" : "submit"}
      disabled={busy}
      onClick={onClick}
      className={cx(
        "w-full rounded-xl px-4 py-3 text-sm font-bold text-white shadow-sm transition disabled:opacity-50",
        colors[color]
      )}
    >
      {busy ? (
        <span className="flex items-center justify-center gap-2">
          <Loader2 className="h-4 w-4 animate-spin" />
          Wird ausgeführt…
        </span>
      ) : label}
    </button>
  );
}

function OptionRow({ children }) {
  return <div className="flex flex-wrap items-center gap-4">{children}</div>;
}

function Check({ name, label, defaultChecked }) {
  return (
    <label className="flex items-center gap-2 text-sm text-slate-600">
      <input name={name} type="checkbox" defaultChecked={defaultChecked} className="rounded" />
      {label}
    </label>
  );
}

function HeapInput() {
  return (
    <label className="flex items-center gap-2 text-sm text-slate-600">
      Heap MB
      <input name="heapMb" type="number" min={256} max={8192} defaultValue={1500}
        className="w-20 rounded-lg border border-slate-300 px-2 py-1 text-sm" />
    </label>
  );
}

function InfoCard({ title, children }) {
  return (
    <div className="rounded-xl border border-slate-200 bg-slate-50 p-4">
      <h4 className="mb-2 text-xs font-bold uppercase tracking-wider text-slate-500">{title}</h4>
      {children}
    </div>
  );
}

function BackendHint() {
  return (
    <div className="rounded-lg border border-amber-200 bg-amber-50 px-3 py-2 text-xs text-amber-800">
      <strong>Hinweis:</strong> Kein Backend konfiguriert. Setze{" "}
      <code className="rounded bg-amber-100 px-1 font-mono">NEXT_PUBLIC_RKSV_BACKEND_URL</code>{" "}
      für Streaming-Verifizierung via externem Java-Backend (z.B. Cloud Run).
      Ohne Backend wird die lokale API verwendet (benötigt Java auf dem Server).
    </div>
  );
}
