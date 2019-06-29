package sample.Util.DepLogic;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import sample.Util.CryptoTools;
import sample.Util.Receipt;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class DecryptionLogic {

    public TestData checkGroupOfReceipt(Receipt[] parts , TestData testData) throws IOException, NoSuchAlgorithmException, ParseException {
        for (int i = 0; i < parts.length; i++) {
            Receipt receiptToTest = parts[i];
            testData = checkAndPrintReceipt(receiptToTest, testData);
        }
        return testData;
    }

    public TestData checkAndPrintReceipt(Receipt receiptToTest, TestData testData) throws IOException, NoSuchAlgorithmException, ParseException {
        if (receiptToTest != null) {
            int wrongSetValues = receiptToTest.calculateNumberValuesOfReceiptStrings();

            if (receiptToTest.getReceiptNumber() == 0 && !testData.isFristReceiptNotIncluded) {
                receiptToTest.calculatePreviousAndNextSignitarues(receiptToTest.getRegisterId());
            } else {
                receiptToTest.calculatePreviousAndNextSignitarues(testData.oldSignature);
            }

            testData.oldRevenueValue = receiptToTest.calculateRevenueShouldBe(testData.oldRevenueValue, testData.cryptoFileLocation, testData.isFristReceiptNotIncluded, testData.errorBlocker);

            if (testData.errorBlocker && receiptToTest.wasErrorBlockerUsed()) {
                testData.errorBlocker = false;
            }
            testData.resultFile.write(receiptToTest.toString().getBytes());
            //Tests
            testData.testResult.addChainedReceipt(receiptToTest.getReceiptNumber(), receiptToTest.isProperChained());
            testData.testResult.addRevenueSet(receiptToTest.getReceiptNumber(), receiptToTest.isRevenueProperEncrypted());
            if (isDateProperFormated(receiptToTest.getReceiptDate())) {
                if (testData.oldDate != null && !isDateProperChained(receiptToTest.getReceiptDate(), testData.oldDate)) {
                    testData.testResult.addWrongChainedDate(receiptToTest.getReceiptNumber());
                    testData.resultFile.write("Datumverkettung: FEHLER\r\n".getBytes());
                } else {
                    testData.oldDate = receiptToTest.getReceiptDate();
                }
            } else {
                testData.testResult.addWrongDate(receiptToTest.getReceiptNumber());
                testData.resultFile.write("Datumsformat: FEHLER\r\n".getBytes());
            }

            if (wrongSetValues > 0) {
                testData.testResult.addWrongSetValue(receiptToTest.getReceiptNumber());
                testData.resultFile.write("Betragsspalte: FEHLER\r\n".getBytes());
            }
            if (testData.allReceiptIds.contains(receiptToTest.getReceiptId())) {
                testData.testResult.addWrongReceiptId(receiptToTest.getReceiptNumber());
                testData.resultFile.write("Belegnummer: FEHLER\r\n".getBytes());
            } else {
                testData.allReceiptIds.add(receiptToTest.getReceiptId());
            }
            testData.resultFile.write("\r\n".getBytes());
            testData.oldDate = receiptToTest.getReceiptDate();
            testData.oldSignature = receiptToTest.getWholeReceipt();

        } else {
            testData.testResult.addWrongStructureValues(receiptToTest.getReceiptNumber());
        }
        return testData;
    }
    //Helper methods
    public Receipt DepStringToReceipt(int receiptNumber, String input) throws UnsupportedEncodingException {
        CryptoTools cryptoTools = new CryptoTools();

        input = input.substring(input.indexOf("\"") + 1);
        input = input.substring(0, input.indexOf("\""));
        String[] inputparts = input.split("[.]");

        if (inputparts.length > 1) {
            byte[] payload = cryptoTools.base64Decode(inputparts[1], false);
            String payloadParts = new String(payload, "UTF-8");
            Receipt receipt = StringToReceipt(receiptNumber, payloadParts);
            if (receipt != null) {
                //old calculation might be relevant?

                /*String signatureLoad = cryptoTools.base64UrlDecode(inputparts[2]);
                byte[] encodedBytes = Base64.encodeBase64(signatureLoad.getBytes());
                String signature = new String(encodedBytes, "UTF-8");
                receipt.setSignature(signature);*/

                receipt.setSignature(inputparts[2]);
                receipt.setWholeReceipt(input);
                return receipt;
            }
        }
        return null;

    }

    public Receipt QrStringToReceipt(int receiptNumber, String input) throws UnsupportedEncodingException {


        input = input.substring(input.indexOf("\"") + 1);
        input = input.substring(0, input.indexOf("\""));
        Receipt receipt = StringToReceipt(receiptNumber, input);
        if (receipt != null) {
            String[] receiptParts = input.split("_");
            receipt.setSignature(receiptParts[13]);
            receipt.setWholeReceipt(getDepFormatFromQr(input));
        }
        return receipt;
    }

    public Receipt StringToReceipt(int receiptNumber, String input) {
        Receipt receipt = new Receipt();
        String[] receiptParts = input.split("_");
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
        }
        return receipt;
    }

    public String getDepFormatFromQr(String input) throws UnsupportedEncodingException {
        int index= StringUtils.ordinalIndexOf(input, "_", 13);
        String payload=input.substring(0,index);
        String signature=input.substring(index+1);
        StringBuilder output=new StringBuilder();
        output.append("eyJhbGciOiJFUzI1NiJ9.");
        CryptoTools cryptoTools = new CryptoTools();
        output.append(cryptoTools.base64UrlEncode(payload).replace("\r\n",""));
        output.append(".");
        signature = signature.replace('+','-');
        signature = signature.replace('/','_');
        signature=signature.replaceAll("==","");
        output.append(signature);
        return output.toString();
    }

    public Receipt[] convertDepReceiptsToReceipts(String[] parts) throws UnsupportedEncodingException {
        Receipt[] output = new Receipt[parts.length];
        for (int i = 0; i < parts.length; i++) {
            output[i] = DepStringToReceipt(i, parts[i]);
        }
        return output;
    }

    public Receipt[] convertQrInputToReceipts(String input) throws UnsupportedEncodingException {
        Receipt[] output = new Receipt[0];
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonArray = (JsonArray) jsonParser.parse(input);
        if (jsonArray != null) {
            int len = jsonArray.size();
            output = new Receipt[len];
            for (int i = 0; i < len; i++) {
                output[i] = QrStringToReceipt(i, jsonArray.get(i).toString());
            }
        }
        return output;
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
