package sample.Util;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;

public class DepTest {
    IOTools ioTools = new IOTools(new Configuration());
    CryptoTools cryptoTools = new CryptoTools();

    //Run DEP-Test
    public String runDepTest(String DefaultStringDEP, String DefaultStringCRYPTO, boolean futurBox, String outputFile, boolean DetailsBox, Configuration config) {
        StringBuilder outputstring = new StringBuilder();

        Process process = null;
        IOTools iOTools = new IOTools(config);
        String processString = iOTools.createDepProcessString(DefaultStringDEP, DefaultStringCRYPTO, outputFile, futurBox, DetailsBox);

        try {
            process = Runtime.getRuntime().exec(processString);
        } catch (IOException e) {
            //TODO: use BMD Exeption
            System.out.println("Error while calling regkassen-verification-depformat-1.1.1.jar on __ShowDEPFileInConsole.java on Line 290");
            e.printStackTrace();
        }
        InputStream depToolOutputStream = process.getInputStream();
        InputStreamReader depToolOutputStreamReader = new InputStreamReader(depToolOutputStream);
        BufferedReader br = new BufferedReader(depToolOutputStreamReader);
        String line;

        /*Funktionsblock zum schreiben auf die JTextaera
         da die Jtextarea eine Character begrenzung in der Weite hat
         (~~~105 Chars) und es Zeilen gibt die mehr beanspruchen
         muss zuerst geprüft werden ob die Zeile größer ist. Wenn Sie
         größer ist wird sie soo oft geteilt auf die JTextarea
         geschrieben
         bis keine Chars mehr vorhanden sind.
         nach jeder geschriebenen Zeile wird die JTextarea um eine
         "row" erweiterd
         Am schluss wird der Courser wieder ganz am Anfang gestellt*/

        try {
            while ((line = br.readLine()) != null) {

                if (line.length() > 105) {
                    int lineCounter = line.length();
                    int whileFlag = 0;
                    while (lineCounter - 105 > 0) {
                        outputstring.append(line.substring(whileFlag, whileFlag + 105) + "\r\n");
                        whileFlag = whileFlag + 105;
                        lineCounter = lineCounter - 105;
                    }
                    outputstring.append(line.substring(whileFlag, line.length()) + "\r\n");
                } else {
                    outputstring.append(line + "\r\n");
                }
            }
        } catch (IOException e) {
            // TODO BMD Exeptions
            e.printStackTrace();
        }
        return outputstring.toString();
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

    public String decryptAndStructureDepFile(String depFileLocation, String cryptoFileLocation, boolean isFristReceiptNotIncluded) throws IOException, NoSuchAlgorithmException, ParseException {
        //TODO: delte me add this ti DEP-TEST!
        //TODO: how can i make this code actually look good as well?
        File file = new File("delteme.txt");
        file.createNewFile();
        FileOutputStream out = new FileOutputStream("delteme.txt");
        DepTestResult depTestResult = new DepTestResult();
        String depFileContent = ioTools.readTxtFile(depFileLocation);
        int nextReceiptField = depFileContent.indexOf("Belege-kompakt");
        while (nextReceiptField > -1) {
            depFileContent = depFileContent.substring(depFileContent.indexOf("Belege-kompakt"), depFileContent.length()); //check reduntant
            String depFileReceipts = depFileContent.substring(depFileContent.indexOf("["), depFileContent.indexOf("]"));
            nextReceiptField = depFileContent.indexOf("Belege-kompakt", depFileContent.indexOf("Belege-kompakt") + 1);

            String[] parts = depFileReceipts.split(",");
            //TODO: check start values!
            double oldRevenueValue = 0;
            String oldSignature = "";
            String oldDate = null;
            HashSet allReceiptIds = new HashSet<String>();

            for (int i = 0; i < parts.length; i++) {
                Receipt r = StringToReceipt(i, parts[i]);
                if (r != null) {
                    int wrongSetValues = r.calculateNumberValuesOfReceiptStrings();
                    if(i == 0 && !isFristReceiptNotIncluded){
                        r.calculatePreviousAndNextSignitarues(r.getRegisterId());
                    }else{
                        r.calculatePreviousAndNextSignitarues(oldSignature);
                    }

                    oldRevenueValue = r.calculateRevenueShouldBe(oldRevenueValue, cryptoFileLocation, isFristReceiptNotIncluded, false);

                    //Tests
                    depTestResult.addChainedReceipt(i,r.isProperChained());
                    depTestResult.addRevenueSet(i, r.isRevenueProperEncrypted());
                    if (isDateProperFormated(r.getReceiptDate())) {
                        if (oldDate != null && !isDateProperChained(r.getReceiptDate(), oldDate)) {
                            depTestResult.addWrongChainedDate(i);
                            out.write("Datumverkettung: FEHLER\r\n".getBytes());
                        } else {
                            oldDate = r.getReceiptDate();
                        }
                    } else {
                        depTestResult.addWrongDate(i);
                    }

                    if (wrongSetValues > 0) {
                        depTestResult.addWrongSetValue(i);
                    }
                    if (allReceiptIds.contains(r.getReceiptId())) {
                        depTestResult.addWrongReceiptId(i);
                    } else {
                        allReceiptIds.add(r.getReceiptId());
                    }

                    oldDate = r.getReceiptDate();
                    oldSignature = r.getWholeReceipt();
                    out.write(r.toString().getBytes());
                } else {
                    depTestResult.addWrongStructureValues(i);
                }
            }
        }
        out.write(depTestResult.printResults().getBytes());
        out.close();
        return null;
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
