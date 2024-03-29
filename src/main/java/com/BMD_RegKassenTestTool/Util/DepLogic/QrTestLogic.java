package com.bmd_regkassentesttool.Util.DepLogic;

import com.bmd_regkassentesttool.Util.Configuration;
import com.bmd_regkassentesttool.Util.DepLogic.Results.ShowResult;
import com.bmd_regkassentesttool.Util.DepLogic.Results.TestResult;
import com.bmd_regkassentesttool.Util.IOTools;
import com.bmd_regkassentesttool.Util.Receipt;

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
    public ShowResult runDepTest(String DefaultStringDEP, String DefaultStringCRYPTO, boolean futurBox, boolean DetailsBox, File outputFile) throws IOException {
        ShowResult showResult = new ShowResult(outputFile);
        FileOutputStream resultFile = new FileOutputStream(outputFile.getPath());

        String decodedPath = URLDecoder.decode(DepTestLogic.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");

        String processString = ioTools.createQrProcessString(DefaultStringDEP, DefaultStringCRYPTO, outputFile.getAbsolutePath(), futurBox, DetailsBox);

        Process process = Runtime.getRuntime().exec(processString);
        InputStream qrToolOutputStream = process.getInputStream();
        InputStreamReader qrToolOutputStreamReader = new InputStreamReader(qrToolOutputStream);
        BufferedReader br = new BufferedReader(qrToolOutputStreamReader);
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
        String qrFileContent = ioTools.readTxtFile(depFileLocation);
        TestData testData = new TestData(0,
                "",
                null,
                new HashSet<String>(),
                isFristReceiptNotIncluded,
                isFristReceiptNotIncluded,
                cryptoFileLocation,
                new TestResult(outputLocation),
                resultFile);
        //decrypt
        Receipt[] receipts = decryptionLogic.convertQrInputToReceipts(qrFileContent);
        //actual Test
        testData = decryptionLogic.checkGroupOfReceipt(receipts, testData);
        //output
        resultFile.write(testData.testResult.printResults().getBytes());
        resultFile.close();
        return testData.testResult;
    }
}
