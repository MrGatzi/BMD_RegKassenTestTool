import { createHash, createDecipheriv } from "node:crypto";

function base64UrlDecode(input) {
  let b64 = input.replace(/-/g, "+").replace(/_/g, "/");
  while (b64.length % 4 !== 0) b64 += "=";
  return Buffer.from(b64, "base64");
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
  if (plain[0] & 0x80) {
    val -= 1n << BigInt(plain.length * 8);
  }
  return val;
}

function parseJwsReceipt(jwsCompact, index) {
  const parts = jwsCompact.split(".");
  if (parts.length < 3) return null;
  const payload = base64UrlDecode(parts[1]).toString("utf-8");
  const fields = payload.split("_");
  if (fields.length < 13) return null;

  return {
    index,
    algorithm: fields[0],
    zda: fields[1],
    registerId: fields[2],
    receiptId: fields[3],
    receiptDate: fields[4],
    setNormal: fields[5],
    setReduced1: fields[6],
    setReduced2: fields[7],
    setNull: fields[8],
    setSpecial: fields[9],
    revenueEncrypted: fields[10],
    certificateSerial: fields[11],
    chainValuePrevious: fields[12],
    signatureRaw: parts[2],
    jwsCompact,
    payload,
  };
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

export function decryptAndStructureDep(depText, cryptoText, options = {}) {
  const { firstReceiptNotIncluded = false } = options;

  const depExport = JSON.parse(depText);
  const groups = depExport["Belege-Gruppe"];
  if (!Array.isArray(groups)) throw new Error('Missing "Belege-Gruppe" in DEP.');

  const aesKey = extractAesKey(cryptoText);

  const allReceipts = [];
  for (const group of groups) {
    const compact = group?.["Belege-kompakt"];
    if (!Array.isArray(compact)) continue;
    for (const jws of compact) {
      if (typeof jws === "string" && jws.trim()) allReceipts.push(jws.trim());
    }
  }

  const receipts = [];
  let oldRevenueValue = 0n;
  let oldSignature = "";
  let oldDate = null;
  const seenIds = new Set();
  let errorBlocker = firstReceiptNotIncluded;

  const summary = {
    totalReceipts: allReceipts.length,
    chainErrors: [],
    revenueErrors: [],
    amountFormatErrors: [],
    structureErrors: [],
    receiptIdErrors: [],
    dateFormatErrors: [],
    dateChainErrors: [],
  };

  for (let i = 0; i < allReceipts.length; i++) {
    const parsed = parseJwsReceipt(allReceipts[i], i);
    if (!parsed) {
      summary.structureErrors.push(i);
      receipts.push({ index: i, error: "Could not parse receipt" });
      continue;
    }

    const setNormalCents = parseGermanDecimal(parsed.setNormal);
    const setReduced1Cents = parseGermanDecimal(parsed.setReduced1);
    const setReduced2Cents = parseGermanDecimal(parsed.setReduced2);
    const setNullCents = parseGermanDecimal(parsed.setNull);
    const setSpecialCents = parseGermanDecimal(parsed.setSpecial);
    const amountErrors = checkAmountFormat(parsed);

    let revenueDecrypted = "";
    let revenueShouldBe = null;
    let revenueOk = true;
    let errorBlockerUsed = false;

    if (parsed.revenueEncrypted === "U1RP") {
      revenueDecrypted = "STO";
      const shouldBe = BigInt(setNormalCents) + BigInt(setReduced1Cents) + BigInt(setReduced2Cents) + BigInt(setNullCents) + BigInt(setSpecialCents) + oldRevenueValue;
      revenueShouldBe = (Number(shouldBe) / 100).toFixed(2);
      oldRevenueValue = shouldBe;
    } else if (parsed.revenueEncrypted === "VFJB") {
      revenueDecrypted = "TRA";
      revenueShouldBe = null;
    } else {
      const decryptedRaw = decryptTurnoverCounter(parsed.revenueEncrypted, parsed.registerId, parsed.receiptId, aesKey);
      revenueDecrypted = (Number(decryptedRaw) / 100).toFixed(2);
      const shouldBe = BigInt(setNormalCents) + BigInt(setReduced1Cents) + BigInt(setReduced2Cents) + BigInt(setNullCents) + BigInt(setSpecialCents) + oldRevenueValue;

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

    let chainPrevCalc, chainNextCalc;
    let chainOk = true;
    if (i === 0 && !firstReceiptNotIncluded) {
      chainPrevCalc = sha256First8Base64(parsed.registerId);
      chainNextCalc = sha256First8Base64(allReceipts[i]);
    } else if (i === 0 && firstReceiptNotIncluded) {
      chainPrevCalc = parsed.chainValuePrevious;
      chainNextCalc = sha256First8Base64(allReceipts[i]);
    } else {
      chainPrevCalc = sha256First8Base64(oldSignature);
      chainNextCalc = sha256First8Base64(allReceipts[i]);
    }
    if (chainPrevCalc !== parsed.chainValuePrevious) chainOk = false;

    let dateOk = true;
    let dateChainOk = true;
    const datePattern = /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}$/;
    if (!datePattern.test(parsed.receiptDate)) {
      dateOk = false;
      summary.dateFormatErrors.push(i);
    } else if (oldDate) {
      const cur = new Date(parsed.receiptDate.replace("T", " "));
      const prev = new Date(oldDate.replace("T", " "));
      if (cur < prev) {
        dateChainOk = false;
        summary.dateChainErrors.push(i);
      }
    }

    if (seenIds.has(parsed.receiptId)) {
      summary.receiptIdErrors.push(i);
    } else {
      seenIds.add(parsed.receiptId);
    }
    if (!chainOk) summary.chainErrors.push(i);
    if (!revenueOk) summary.revenueErrors.push(i);
    if (amountErrors > 0) summary.amountFormatErrors.push(i);

    receipts.push({
      index: i,
      zda: parsed.zda,
      registerId: parsed.registerId,
      receiptId: parsed.receiptId,
      receiptDate: parsed.receiptDate,
      setNormal: parsed.setNormal,
      setReduced1: parsed.setReduced1,
      setReduced2: parsed.setReduced2,
      setNull: parsed.setNull,
      setSpecial: parsed.setSpecial,
      revenueEncrypted: parsed.revenueEncrypted,
      revenueDecrypted,
      revenueShouldBe,
      revenueOk,
      certificateSerial: parsed.certificateSerial,
      chainValuePrevious: parsed.chainValuePrevious,
      chainValuePreviousCalc: chainPrevCalc,
      chainValueNext: chainNextCalc,
      chainOk,
      signature: parsed.signatureRaw,
      dateOk,
      dateChainOk,
    });

    oldDate = parsed.receiptDate;
    oldSignature = allReceipts[i];
  }

  const germanSummary = buildGermanSummary(summary);

  return { receipts, summary, germanSummary };
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
  const total = summary.totalReceipts;
  const chainOkCount = total - summary.chainErrors.length;
  const revenueOkCount = total - summary.revenueErrors.length;
  const lines = [
    `Listen Elemente: ${total} , davon falsch verkettet: ${summary.chainErrors.length}`,
    `Berechnete Umsatzzähler: ${total} , davon falsch verkettet: ${summary.revenueErrors.length}`,
    `Belege mit falschen Betragsspalten: ${summary.amountFormatErrors.length}`,
    `Belege mit falschem Aufbau: ${summary.structureErrors.length}`,
    `Belege mit falscher BelegID: ${summary.receiptIdErrors.length}`,
    `Belege mit falschem Datum: ${summary.dateFormatErrors.length}`,
    `Belege mit falscher Datumsverkettung: ${summary.dateChainErrors.length}`,
  ];
  return lines.join("\n");
}
