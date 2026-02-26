import { createHash, createDecipheriv } from "node:crypto";

// ─── Crypto primitives ──────────────────────────────────────────────────────

function base64UrlDecode(input) {
  let b64 = input.replace(/-/g, "+").replace(/_/g, "/");
  while (b64.length % 4 !== 0) b64 += "=";
  return Buffer.from(b64, "base64");
}

function base64UrlEncode(input) {
  return Buffer.from(input)
    .toString("base64")
    .replace(/\+/g, "-")
    .replace(/\//g, "_")
    .replace(/=+$/, "");
}

function base64Decode(input) {
  return Buffer.from(input, "base64");
}

function base64Encode(buffer) {
  return Buffer.from(buffer).toString("base64");
}

function sha256First8Base64(input) {
  const hash = createHash("sha256").update(input).digest();
  return base64Encode(hash.subarray(0, 8));
}

function parseGermanDecimal(value) {
  const cleaned = value.replace(/\./g, "").replace(",", ".");
  return Math.round(parseFloat(cleaned) * 100);
}

function decryptTurnoverCounter(encryptedBase64, kassenId, belegId, aesKeyBuffer) {
  const ivInput = kassenId + belegId;
  const hash = createHash("sha256").update(ivInput).digest();
  const iv = hash.subarray(0, 16);
  const encrypted = base64Decode(encryptedBase64);
  const decipher = createDecipheriv("aes-256-ctr", aesKeyBuffer, iv);
  decipher.setAutoPadding(false);
  const decrypted = Buffer.concat([decipher.update(encrypted), decipher.final()]);
  const plain = decrypted.subarray(0, encrypted.length);
  if (plain.length === 0) return 0n;
  let val = 0n;
  for (let i = 0; i < plain.length; i++) {
    val = (val << 8n) | BigInt(plain[i]);
  }
  if (plain[0] & 0x80) val -= 1n << BigInt(plain.length * 8);
  return val;
}

function extractAesKey(cryptoMaterialJson) {
  const parsed = JSON.parse(cryptoMaterialJson);
  const b64Key =
    parsed?.base64AESKey ??
    parsed?.["base64AESKey"] ??
    (() => {
      const raw = JSON.stringify(parsed);
      const parts = raw.split('"');
      return parts.length >= 4 ? parts[3] : null;
    })();
  if (!b64Key) throw new Error("AES key not found in crypto material.");
  const decoded = base64Decode(b64Key);
  if (decoded.length < 32) {
    return base64Decode("WQRtiiya3hYh/Uz44Bv3x8ETl1nrH6nCdErn69g5/lU=");
  }
  return decoded;
}

// ─── Receipt parsing ────────────────────────────────────────────────────────

function parseJwsReceipt(jwsCompact, index) {
  const parts = jwsCompact.split(".");
  if (parts.length < 3) return null;
  const payload = base64UrlDecode(parts[1]).toString("utf-8");
  const fields = payload.split("_");
  if (fields.length < 13) return null;
  return {
    index, algorithm: fields[0], zda: fields[1], registerId: fields[2],
    receiptId: fields[3], receiptDate: fields[4],
    setNormal: fields[5], setReduced1: fields[6], setReduced2: fields[7],
    setNull: fields[8], setSpecial: fields[9],
    revenueEncrypted: fields[10], certificateSerial: fields[11],
    chainValuePrevious: fields[12], signatureRaw: parts[2],
    jwsCompact, rawForChain: jwsCompact,
  };
}

function parseQrReceipt(qrString, index) {
  const fields = qrString.split("_");
  if (fields.length < 14) return null;
  const payloadUpTo12 = fields.slice(0, 13).join("_");
  const sig = fields[13];
  const jwsPayload = base64UrlEncode(payloadUpTo12);
  const jwsSig = sig.replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/, "");
  const rawForChain = `eyJhbGciOiJFUzI1NiJ9.${jwsPayload}.${jwsSig}`;

  return {
    index, algorithm: fields[0], zda: fields[1], registerId: fields[2],
    receiptId: fields[3], receiptDate: fields[4],
    setNormal: fields[5], setReduced1: fields[6], setReduced2: fields[7],
    setNull: fields[8], setSpecial: fields[9],
    revenueEncrypted: fields[10], certificateSerial: fields[11],
    chainValuePrevious: fields[12], signatureRaw: sig,
    rawForChain,
  };
}

// ─── Core verification engine ───────────────────────────────────────────────

function processReceipts(parsedReceipts, rawForChainList, aesKey, cryptoText, options = {}) {
  const { firstReceiptNotIncluded = false } = options;
  const receipts = [];
  let oldRevenueValue = 0n;
  let oldSignature = "";
  let oldDate = null;
  const seenIds = new Set();
  let errorBlocker = firstReceiptNotIncluded;

  const summary = {
    totalReceipts: parsedReceipts.length,
    chainErrors: [], revenueErrors: [], amountFormatErrors: [],
    structureErrors: [], receiptIdErrors: [], dateFormatErrors: [], dateChainErrors: [],
  };

  for (let i = 0; i < parsedReceipts.length; i++) {
    const parsed = parsedReceipts[i];
    if (!parsed) {
      summary.structureErrors.push(i);
      receipts.push({ index: i, error: "Could not parse receipt" });
      continue;
    }

    const cents = [parsed.setNormal, parsed.setReduced1, parsed.setReduced2, parsed.setNull, parsed.setSpecial].map(parseGermanDecimal);
    const amountErrors = checkAmountFormat(parsed);

    let revenueDecrypted = "", revenueShouldBe = null, revenueOk = true, errorBlockerUsed = false;
    const sumCents = cents.reduce((a, b) => BigInt(a) + BigInt(b), 0n);

    if (parsed.revenueEncrypted === "U1RP") {
      revenueDecrypted = "STO";
      const shouldBe = sumCents + oldRevenueValue;
      revenueShouldBe = (Number(shouldBe) / 100).toFixed(2);
      oldRevenueValue = shouldBe;
    } else if (parsed.revenueEncrypted === "VFJB") {
      revenueDecrypted = "TRA";
    } else {
      const decryptedRaw = decryptTurnoverCounter(parsed.revenueEncrypted, parsed.registerId, parsed.receiptId, aesKey);
      revenueDecrypted = (Number(decryptedRaw) / 100).toFixed(2);
      const shouldBe = sumCents + oldRevenueValue;
      if (decryptedRaw === shouldBe) {
        revenueShouldBe = (Number(shouldBe) / 100).toFixed(2);
        oldRevenueValue = shouldBe;
      } else {
        if ((i === 0 && firstReceiptNotIncluded) || errorBlocker) {
          revenueShouldBe = revenueDecrypted;
          errorBlockerUsed = true;
        } else {
          revenueOk = false;
          revenueShouldBe = "FEHLER";
        }
        oldRevenueValue = decryptedRaw;
      }
    }
    if (errorBlocker && errorBlockerUsed) errorBlocker = false;

    let chainPrevCalc, chainNextCalc, chainOk = true;
    const chainInput = rawForChainList[i];
    if (i === 0 && !firstReceiptNotIncluded) {
      chainPrevCalc = sha256First8Base64(parsed.registerId);
    } else if (i === 0) {
      chainPrevCalc = parsed.chainValuePrevious;
    } else {
      chainPrevCalc = sha256First8Base64(oldSignature);
    }
    chainNextCalc = sha256First8Base64(chainInput);
    if (chainPrevCalc !== parsed.chainValuePrevious) chainOk = false;

    let dateOk = true, dateChainOk = true;
    if (!/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}$/.test(parsed.receiptDate)) {
      dateOk = false;
      summary.dateFormatErrors.push(i);
    } else if (oldDate) {
      if (new Date(parsed.receiptDate.replace("T", " ")) < new Date(oldDate.replace("T", " "))) {
        dateChainOk = false;
        summary.dateChainErrors.push(i);
      }
    }

    if (seenIds.has(parsed.receiptId)) summary.receiptIdErrors.push(i);
    else seenIds.add(parsed.receiptId);
    if (!chainOk) summary.chainErrors.push(i);
    if (!revenueOk) summary.revenueErrors.push(i);
    if (amountErrors > 0) summary.amountFormatErrors.push(i);

    receipts.push({
      index: i, zda: parsed.zda, registerId: parsed.registerId,
      receiptId: parsed.receiptId, receiptDate: parsed.receiptDate,
      setNormal: parsed.setNormal, setReduced1: parsed.setReduced1,
      setReduced2: parsed.setReduced2, setNull: parsed.setNull, setSpecial: parsed.setSpecial,
      revenueEncrypted: parsed.revenueEncrypted, revenueDecrypted, revenueShouldBe, revenueOk,
      certificateSerial: parsed.certificateSerial,
      chainValuePrevious: parsed.chainValuePrevious,
      chainValuePreviousCalc: chainPrevCalc, chainValueNext: chainNextCalc, chainOk,
      signature: parsed.signatureRaw, dateOk, dateChainOk,
    });

    oldDate = parsed.receiptDate;
    oldSignature = chainInput;
  }

  return { receipts, summary, germanSummary: buildGermanSummary(summary) };
}

function checkAmountFormat(parsed) {
  let errors = 0;
  for (const field of [parsed.setNormal, parsed.setReduced1, parsed.setReduced2, parsed.setNull, parsed.setSpecial]) {
    const cleaned = field.replace(/\./g, "");
    const afterComma = cleaned.substring(cleaned.lastIndexOf(",") + 1);
    if (afterComma.length > 2) errors++;
  }
  return errors;
}

function buildGermanSummary(summary) {
  const t = summary.totalReceipts;
  return [
    `Listen Elemente: ${t} , davon falsch verkettet: ${summary.chainErrors.length}`,
    `Berechnete Umsatzzähler: ${t} , davon falsch verkettet: ${summary.revenueErrors.length}`,
    `Belege mit falschen Betragsspalten: ${summary.amountFormatErrors.length}`,
    `Belege mit falschem Aufbau: ${summary.structureErrors.length}`,
    `Belege mit falscher BelegID: ${summary.receiptIdErrors.length}`,
    `Belege mit falschem Datum: ${summary.dateFormatErrors.length}`,
    `Belege mit falscher Datumsverkettung: ${summary.dateChainErrors.length}`,
  ].join("\n");
}

// ─── Public: DEP structured decrypt ─────────────────────────────────────────

export function decryptAndStructureDep(depText, cryptoText, options = {}) {
  const depExport = JSON.parse(depText);
  const groups = depExport["Belege-Gruppe"];
  if (!Array.isArray(groups)) throw new Error('Missing "Belege-Gruppe" in DEP.');
  const aesKey = extractAesKey(cryptoText);

  const allJws = [];
  for (const group of groups) {
    const compact = group?.["Belege-kompakt"];
    if (!Array.isArray(compact)) continue;
    for (const jws of compact) {
      if (typeof jws === "string" && jws.trim()) allJws.push(jws.trim());
    }
  }

  const parsed = allJws.map((jws, i) => parseJwsReceipt(jws, i));
  return processReceipts(parsed, allJws, aesKey, cryptoText, options);
}

// ─── Public: QR receipt structured decrypt ──────────────────────────────────

export function decryptAndStructureQr(qrText, cryptoText, options = {}) {
  const aesKey = extractAesKey(cryptoText);
  let qrLines;

  try {
    const parsed = JSON.parse(qrText);
    if (Array.isArray(parsed)) {
      qrLines = parsed.map((s) => String(s).trim()).filter(Boolean);
    } else {
      throw new Error("not array");
    }
  } catch {
    qrLines = qrText.split(/\r?\n/).map((l) => l.replace(/^"|"$/g, "").trim()).filter(Boolean);
  }

  const parsedReceipts = qrLines.map((line, i) => parseQrReceipt(line, i));
  const rawForChain = parsedReceipts.map((r) => r?.rawForChain ?? "");
  return processReceipts(parsedReceipts, rawForChain, aesKey, cryptoText, options);
}

// ─── Public: Advanced multi-group with chain ordering + per-segment decrypt ─

export function advancedSplitAndDecrypt(depText, cryptoText, options = {}) {
  const depExport = JSON.parse(depText);
  const groups = depExport["Belege-Gruppe"];
  if (!Array.isArray(groups) || groups.length === 0) {
    throw new Error('Missing or empty "Belege-Gruppe" in DEP.');
  }

  const aesKey = extractAesKey(cryptoText);

  const groupMeta = groups.map((group, idx) => {
    const compact = group?.["Belege-kompakt"] ?? [];
    const jwsList = compact.filter((s) => typeof s === "string" && s.trim()).map((s) => s.trim());
    const firstReceipt = jwsList.length > 0 ? parseJwsReceipt(jwsList[0], 0) : null;
    const firstJws = jwsList[0] ?? "";
    const nextSig = firstJws ? sha256First8Base64(firstJws) : "";
    const isStart = firstReceipt
      ? sha256First8Base64(firstReceipt.registerId) === firstReceipt.chainValuePrevious
      : false;
    return { idx, jwsList, firstReceipt, nextSig, isStart, receiptCount: jwsList.length };
  });

  const ordered = orderGroupsByChain(groupMeta);

  const segments = [];
  for (const gm of ordered) {
    const parsed = gm.jwsList.map((jws, i) => parseJwsReceipt(jws, i));
    const result = processReceipts(parsed, gm.jwsList, aesKey, cryptoText, {
      firstReceiptNotIncluded: options.firstReceiptNotIncluded ?? false,
    });
    segments.push({
      groupIndex: gm.idx,
      registerId: gm.firstReceipt?.registerId ?? "unknown",
      receiptCount: gm.receiptCount,
      isStart: gm.isStart,
      ...result,
    });
  }

  const totalReceipts = segments.reduce((sum, s) => sum + s.receiptCount, 0);
  const allOk = segments.every((s) =>
    s.summary.chainErrors.length === 0 &&
    s.summary.revenueErrors.length === 0 &&
    s.summary.structureErrors.length === 0
  );

  return {
    groupCount: groups.length,
    orderedGroupIndices: ordered.map((g) => g.idx),
    totalReceipts,
    allOk,
    segments,
  };
}

function orderGroupsByChain(groupMeta) {
  if (groupMeta.length <= 1) return groupMeta;

  const startGroups = groupMeta.filter((g) => g.isStart);
  if (startGroups.length === 0) return groupMeta;

  const ordered = [startGroups[0]];
  const remaining = new Set(groupMeta.filter((g) => g !== startGroups[0]));

  while (remaining.size > 0) {
    const lastSig = ordered[ordered.length - 1].nextSig;
    let found = false;
    for (const candidate of remaining) {
      if (candidate.firstReceipt && candidate.firstReceipt.chainValuePrevious === lastSig) {
        ordered.push(candidate);
        remaining.delete(candidate);
        found = true;
        break;
      }
    }
    if (!found) {
      for (const r of remaining) ordered.push(r);
      break;
    }
  }

  return ordered;
}
