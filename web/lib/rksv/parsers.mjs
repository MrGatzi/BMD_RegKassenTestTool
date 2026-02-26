export function parseDepExportText(depText) {
  let parsed;
  try {
    parsed = JSON.parse(depText);
  } catch {
    throw new Error("DEP file is not valid JSON.");
  }

  if (!parsed || typeof parsed !== "object") {
    throw new Error("DEP file must contain a JSON object.");
  }

  const groups = parsed["Belege-Gruppe"];
  if (!Array.isArray(groups)) {
    throw new Error('DEP JSON is missing "Belege-Gruppe".');
  }

  return parsed;
}

export function extractReceiptsFromDep(depExport) {
  const groups = depExport["Belege-Gruppe"];
  if (!Array.isArray(groups)) {
    return [];
  }

  const receipts = [];
  for (const group of groups) {
    const compact = group?.["Belege-kompakt"];
    if (!Array.isArray(compact)) {
      continue;
    }
    for (const receipt of compact) {
      if (typeof receipt === "string" && receipt.trim()) {
        receipts.push(receipt);
      }
    }
  }

  return receipts;
}

export function normalizeReceiptInput(receiptText) {
  const trimmed = receiptText.trim();
  if (!trimmed) {
    throw new Error("Receipt input is empty.");
  }

  try {
    const parsed = JSON.parse(trimmed);
    if (Array.isArray(parsed)) {
      return parsed
        .map((entry) => String(entry).trim())
        .filter((entry) => entry.length > 0);
    }

    if (parsed && typeof parsed === "object") {
      const fromDep = extractReceiptsFromDep(parsed);
      if (fromDep.length > 0) {
        return fromDep;
      }
    }
  } catch {
    // This may be line-separated raw receipts.
  }

  const lines = trimmed
    .split(/\r?\n/)
    .map((line) => line.trim())
    .filter((line) => line.length > 0);

  if (lines.length === 0) {
    throw new Error("No receipts found in the provided input.");
  }

  return lines;
}

export function splitDepByGroups(depExport) {
  const groups = depExport["Belege-Gruppe"];
  if (!Array.isArray(groups) || groups.length === 0) {
    return [];
  }

  return groups.map((group, index) => {
    const receiptCount = Array.isArray(group?.["Belege-kompakt"])
      ? group["Belege-kompakt"].length
      : 0;
    return {
      index,
      receiptCount,
      depExport: {
        ...depExport,
        "Belege-Gruppe": [group],
      },
    };
  });
}
