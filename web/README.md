# RKSV Verification Web Tool (Next.js)

Web-first port of the legacy Java desktop wrapper around:

- `regkassen-verification-depformat-1.1.1.jar`
- `regkassen-verification-receipts-1.1.1.jar`

The app provides a modern UI and API endpoints for:

1. DEP export verification
2. Single-receipt (QR machine-readable code) verification
3. Advanced DEP split + optional per-segment verification
4. Official workflow self-test using generated RKSV demo data

---

## Public references and documentation

- BMF RKSV mustercode repository:  
  https://github.com/BMF-RKSV-Technik/at-registrierkassen-mustercode
- Prüftool v1.1.1 release:  
  https://github.com/BMF-RKSV-Technik/at-registrierkassen-mustercode/releases/tag/V1.1.1
- Demo/test-data release (includes demo generator):  
  https://github.com/BMF-RKSV-Technik/at-registrierkassen-mustercode/releases/tag/V1.0.0

---

## Local run

```bash
npm install
npm run dev
```

Open `http://localhost:3000`.

---

## Required runtime dependencies

- Node.js (for Next.js)
- Java runtime (`java` in PATH) for verifier routes and workflow self-test

The verifier routes execute the jar tools on the server side.

### Optional environment variables

- `RKSV_DEP_JAR_PATH`  
  Custom path to `regkassen-verification-depformat-1.1.1.jar`
- `RKSV_RECEIPTS_JAR_PATH`  
  Custom path to `regkassen-verification-receipts-1.1.1.jar`
- `RKSV_DEMO_CACHE_DIR`  
  Cache directory for downloaded demo generator archive used by self-test

If unset, the app searches for verifier jars in:

- current working directory
- parent directory

---

## Workflow validation (generated official test data)

Run a full self-test:

```bash
npm run verify:workflows
```

This will:

1. Download/extract the official `regkassen-demo-1.0.0.zip` if needed
2. Generate scenario data in headless mode
3. Verify generated DEP data with `depformat` jar
4. Verify generated QR data with `receipts` jar
5. Run advanced split + per-segment verification

---

## API endpoints

- `POST /api/verify/dep`
- `POST /api/verify/receipts`
- `POST /api/verify/advanced`
- `POST /api/workflows/self-test`

All file-based endpoints accept uploaded `.json`, `.gz` and `.zip`.

---

## Vercel hosting note

The UI can be deployed to Vercel directly.

The verification API routes require Java + local jar execution. If your Vercel runtime does not provide Java, host verifier execution in a separate backend service and connect this frontend to that backend.
