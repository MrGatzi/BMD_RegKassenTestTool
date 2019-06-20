package sample.Util;

import java.io.*;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;

public class DepTestLogic {

    IOTools ioTools;
    CryptoTools cryptoTools;

    public DepTestLogic(Configuration config) {
        ioTools = new IOTools(config);
        cryptoTools = new CryptoTools();
    }


    //Run DEP-Test
    public DepShowResult runDepTest(String DefaultStringDEP, String DefaultStringCRYPTO, boolean futurBox, boolean DetailsBox, File outputFile) throws IOException {
        DepShowResult depShowResult = new DepShowResult(outputFile);
        FileOutputStream resultFile = new FileOutputStream(outputFile.getPath());

        String decodedPath = URLDecoder.decode(DepTestLogic.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");

        String processString = ioTools.createDepProcessString(DefaultStringDEP, DefaultStringCRYPTO, outputFile.getAbsolutePath(), futurBox, DetailsBox);

        Process process = Runtime.getRuntime().exec(processString);
        InputStream depToolOutputStream = process.getInputStream();
        InputStreamReader depToolOutputStreamReader = new InputStreamReader(depToolOutputStream);
        BufferedReader br = new BufferedReader(depToolOutputStreamReader);
        String line;
        while ((line = br.readLine()) != null) {

            if (line.length() > 105) {
                int lineCounter = line.length();
                int whileFlag = 0;
                while (lineCounter - 105 > 0) {
                    resultFile.write((line.substring(whileFlag, whileFlag + 105) + "\r\n").getBytes());
                    whileFlag = whileFlag + 105;
                    lineCounter = lineCounter - 105;
                }
                resultFile.write((line.substring(whileFlag, line.length()) + "\r\n").getBytes());
            } else {
                resultFile.write((line + "\r\n").getBytes());
            }
        }
        resultFile.close();
        return depShowResult;
    }

    public Receipt StringToReceipt(int receiptNumber, String input) throws UnsupportedEncodingException {
        Receipt receipt = new Receipt();

        input = input.substring(input.indexOf("\"") + 1);
        input = input.substring(0, input.indexOf("\""));
        String[] inputparts = input.split("[.]");

        byte[] payload = null;
        if (inputparts.length > 1) {
            payload = cryptoTools.base64Decode(inputparts[1], false);
            String payloadParts = new String(payload, "UTF-8");
            String[] receiptParts = payloadParts.split("_");
            if (receiptParts.length > 11) {
                receipt.setWholeReceipt(input);
                receipt.setReceiptNumber(receiptNumber);
                receipt.setZda(receiptParts[1]);
                receipt.setRegisterId(receiptParts[2]);
                receipt.setReceiptId(receiptParts[3]);
                receipt.setReceiptId(receiptParts[3]);
                receipt.setReceiptDate(receiptParts[4]);

                receipt.setReceiptSetNormal(receiptParts[5]);
                receipt.setReceiptSetReduced1(receiptParts[6]);
                receipt.setReceiptSetReduced2(receiptParts[7]);
                receipt.setReceiptSetNull(receiptParts[8]);
                receipt.setReceiptSetSpecial(receiptParts[9]);

                receipt.setRevenueEncrypted(receiptParts[10]);
                receipt.setCertificateNumber(receiptParts[11]);
                receipt.setSignaturePreviousValue(receiptParts[12]);

                String signatureLoad = cryptoTools.base64UrlDecode(inputparts[2]);
                byte[] encodedBytes = Base64.encodeBase64(signatureLoad.getBytes());
                String signature = new String(encodedBytes, "UTF-8");
                receipt.setSignature(signature);
                return receipt;
            }
        }
        return null;
    }

    public DepTestResult decryptAndStructureDepFile(String depFileLocation, String cryptoFileLocation, boolean isFristReceiptNotIncluded, File outputLocation) throws IOException, NoSuchAlgorithmException, ParseException {
        DepTestResult depTestResult = new DepTestResult(outputLocation);
        FileOutputStream resultFile = new FileOutputStream(outputLocation.getPath());
        String depFileContent = ioTools.readTxtFile(depFileLocation);
        int nextReceiptField = depFileContent.indexOf("Belege-kompakt");

        while (nextReceiptField > -1) {
            depFileContent = depFileContent.substring(depFileContent.indexOf("Belege-kompakt"), depFileContent.length()); //check reduntant
            String depFileReceipts = depFileContent.substring(depFileContent.indexOf("["), depFileContent.indexOf("]"));
            nextReceiptField = depFileContent.indexOf("Belege-kompakt", depFileContent.indexOf("Belege-kompakt") + 1);

            String[] parts = depFileReceipts.split(",");
            double oldRevenueValue = 0;
            String oldSignature = "";
            String oldDate = null;
            HashSet allReceiptIds = new HashSet<String>();
            boolean errorBlocker = isFristReceiptNotIncluded;

            for (int i = 0; i < parts.length; i++) {
                Receipt r = StringToReceipt(i, parts[i]);
                if (r != null) {
                    int wrongSetValues = r.calculateNumberValuesOfReceiptStrings();
                    if (i == 0 && !isFristReceiptNotIncluded) {
                        r.calculatePreviousAndNextSignitarues(r.getRegisterId());
                    } else {
                        r.calculatePreviousAndNextSignitarues(oldSignature);
                    }

                    oldRevenueValue = r.calculateRevenueShouldBe(oldRevenueValue, cryptoFileLocation, isFristReceiptNotIncluded, errorBlocker);

                    if (errorBlocker && r.wasErrorBlockerUsed()) {
                        errorBlocker = false;
                    }
                    resultFile.write(r.toString().getBytes());
                    //Tests
                    depTestResult.addChainedReceipt(i, r.isProperChained());
                    depTestResult.addRevenueSet(i, r.isRevenueProperEncrypted());
                    if (isDateProperFormated(r.getReceiptDate())) {
                        if (oldDate != null && !isDateProperChained(r.getReceiptDate(), oldDate)) {
                            depTestResult.addWrongChainedDate(i);
                            resultFile.write("Datumverkettung: FEHLER\r\n".getBytes());
                        } else {
                            oldDate = r.getReceiptDate();
                        }
                    } else {
                        depTestResult.addWrongDate(i);
                        resultFile.write("Datumsformat: FEHLER\r\n".getBytes());
                    }

                    if (wrongSetValues > 0) {
                        depTestResult.addWrongSetValue(i);
                        resultFile.write("Betragsspalte: FEHLER\r\n".getBytes());
                    }
                    if (allReceiptIds.contains(r.getReceiptId())) {
                        depTestResult.addWrongReceiptId(i);
                        resultFile.write("BelegNummer: FEHLER\r\n".getBytes());
                    } else {
                        allReceiptIds.add(r.getReceiptId());
                    }

                    oldDate = r.getReceiptDate();
                    oldSignature = r.getWholeReceipt();

                } else {
                    depTestResult.addWrongStructureValues(i);
                }
            }
        }
        resultFile.write(depTestResult.printResults().getBytes());
        resultFile.close();
        return depTestResult;
    }

    //checks
    public boolean isDateProperFormated(String input) throws ParseException {
        if (Pattern.matches("\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d", input)) {
            return true;
        }
        return false;
    }

    public boolean isDateProperChained(String currentReceiptDate, String oldReceiptDate) throws ParseException {
        String currentDateString = currentReceiptDate.replace('T', ' ');
        Date currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(currentDateString);

        String oldDateString = oldReceiptDate.replace('T', ' ');
        Date oldDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(oldDateString);

        return currentDate.compareTo(oldDate) > 0;
    }

}
