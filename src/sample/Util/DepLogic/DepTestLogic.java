package sample.Util.DepLogic;

import java.io.*;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.HashSet;

import sample.Util.*;
import sample.Util.DepLogic.Results.ShowResult;
import sample.Util.DepLogic.Results.TestResult;
import sample.Util.DepLogic.Helper.LogicInput;
import sample.Util.DepLogic.Helper.LogicOutput;

public class DepTestLogic {

    IOTools ioTools;
    DecryptionLogic decryptionLogic;

    public DepTestLogic(Configuration config) {
        ioTools = new IOTools(config);
        decryptionLogic = new DecryptionLogic();
    }


    //Run DEP-Test
    public ShowResult runDepTest(String DefaultStringDEP, String DefaultStringCRYPTO, boolean futurBox, boolean DetailsBox, File outputFile) throws IOException {
        ShowResult showResult = new ShowResult(outputFile);
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
        return showResult;
    }

    public TestResult decryptAndStructureDepFile(String depFileLocation, String cryptoFileLocation, boolean isFristReceiptNotIncluded, File outputLocation) throws IOException, NoSuchAlgorithmException, ParseException {
        //prepare
        FileOutputStream resultFile = new FileOutputStream(outputLocation.getPath());
        String depFileContent = ioTools.readTxtFile(depFileLocation);
        LogicInput logicInput = new LogicInput(0,
                "",
                null,
                new HashSet<String>(),
                isFristReceiptNotIncluded,
                isFristReceiptNotIncluded,
                cryptoFileLocation,
                new TestResult(outputLocation),
                resultFile);

        int nextReceiptField = depFileContent.indexOf("Belege-kompakt");
        //muliple differnetDepFile in one
        while (nextReceiptField > -1) {
            //prepare
            depFileContent = depFileContent.substring(depFileContent.indexOf("Belege-kompakt"), depFileContent.length()); //check reduntant
            String depFileReceipts = depFileContent.substring(depFileContent.indexOf("["), depFileContent.indexOf("]"));
            nextReceiptField = depFileContent.indexOf("Belege-kompakt", depFileContent.indexOf("Belege-kompakt") + 1);
            String[] parts = depFileReceipts.split(",");
            //decrypt
            Receipt[] receipts = decryptionLogic.convertDepReceiptsToReceipts(parts);
            //actual Test
            logicInput = decryptionLogic.checkGroupOfReceipt(receipts,logicInput);
        }
        //output
        resultFile.write(logicInput.testResult.printResults().getBytes());
        resultFile.close();
        return logicInput.testResult;
    }

}
