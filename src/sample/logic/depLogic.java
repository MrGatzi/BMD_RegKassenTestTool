package sample.logic;

import java.io.*;
import java.math.BigInteger;

import sample.Util.Configuration;
import sample.Util.IOTools;
import sample.Util.Receipt;

public class depLogic {

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

    public Receipt StringToReceipt(String input) {
        String[] receiptParts = input.split("_");
        Receipt receipt = new Receipt();
        BigInteger numBig;

        receipt.setRegisterId(receiptParts[2]);
        receipt.setReceiptId(receiptParts[3]);
        receipt.setReceiptId(receiptParts[3]);
        receipt.setReceiptDate(receiptParts[4]);

        numBig = new BigInteger(receiptParts[5].replaceAll("\\.", "").replaceAll(",", ""));
        receipt.setReceiptSetNormal(numBig.doubleValue());

        numBig = new BigInteger(receiptParts[6].replaceAll("\\.", "").replaceAll(",", ""));
        receipt.setReceiptSetReduced1(numBig.doubleValue());

        numBig = new BigInteger(receiptParts[7].replaceAll("\\.", "").replaceAll(",", ""));
        receipt.setReceiptSetReduced2(numBig.doubleValue());

        numBig = new BigInteger(receiptParts[8].replaceAll("\\.", "").replaceAll(",", ""));
        receipt.setReceiptSetNull(numBig.doubleValue());

        numBig = new BigInteger(receiptParts[9].replaceAll("\\.", "").replaceAll(",", ""));
        receipt.setReceiptSetSpecial(numBig.doubleValue());

        return receipt;
    }
}
