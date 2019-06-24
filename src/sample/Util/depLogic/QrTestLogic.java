package sample.Util.depLogic;

import sample.Util.Configuration;
import sample.Util.IOTools;
import sample.Util.Receipt;

import java.io.*;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.HashSet;

public class QrTestLogic {
    IOTools ioTools;
    DecryptionLogic decryptionLogic;

    public QrTestLogic(Configuration config) {
        ioTools = new IOTools(config);
        decryptionLogic = new DecryptionLogic();
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

    public DepTestResult decryptAndStructureDepFile(String depFileLocation, String cryptoFileLocation, boolean isFristReceiptNotIncluded, File outputLocation) throws IOException, NoSuchAlgorithmException, ParseException {
        DepTestResult depTestResult = new DepTestResult(outputLocation);
        FileOutputStream resultFile = new FileOutputStream(outputLocation.getPath());
        String qrFileContent = ioTools.readTxtFile(depFileLocation);

        double oldRevenueValue = 0;
        String oldSignature = "";
        String oldDate = null;
        HashSet allReceiptIds = new HashSet<String>();
        boolean errorBlocker = isFristReceiptNotIncluded;

        Receipt[] receipts = decryptionLogic.convertQrInputToReceipts(qrFileContent);
        //actual Test
        LogicInput logicInput = new LogicInput(receipts, oldRevenueValue, oldSignature, oldDate, allReceiptIds, errorBlocker, isFristReceiptNotIncluded, cryptoFileLocation, depTestResult, resultFile);
        LogicOutput logicOutput = decryptionLogic.decryptParts(logicInput);
        //output
        isFristReceiptNotIncluded = logicOutput.isFristReceiptNotIncluded;
        oldRevenueValue = logicOutput.oldRevenueValue;
        oldSignature = logicOutput.oldSignature;
        oldDate = logicOutput.oldDate;
        allReceiptIds = logicOutput.allReceiptIds;
        errorBlocker = logicOutput.errorBlocker;
        depTestResult = logicOutput.depTestResult;

        resultFile.write(depTestResult.printResults().getBytes());
        resultFile.close();
        return depTestResult;
    }
}
