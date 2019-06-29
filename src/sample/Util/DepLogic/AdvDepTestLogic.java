package sample.Util.DepLogic;

import org.apache.commons.codec.binary.Base64;
import sample.Util.Configuration;
import sample.Util.CryptoTools;
import sample.Util.DepLogic.Results.AdvResult;
import sample.Util.DepLogic.Results.TestResult;
import sample.Util.Factories.TmpFactory;
import sample.Util.IOTools;
import sample.Util.Receipt;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@SuppressWarnings("ALL")
public class AdvDepTestLogic {
    Configuration config;
    TmpFactory tmpFactory;
    DecryptionLogic decryptionLogic;
    IOTools ioTools;

    public AdvDepTestLogic(Configuration config) {
        this.config = config;
        this.decryptionLogic = new DecryptionLogic();
        this.tmpFactory = new TmpFactory(config);
        this.ioTools = new IOTools(config);
    }

    public AdvResult runWithDEP(String depFileLocation, String cryptoFileLocation, boolean isFristReceiptNotIncluded, File outputLocation, boolean validFutureDates, boolean showDetails, boolean runDepTests) throws IOException, ParseException {
        AdvResult advResult = new AdvResult(outputLocation);
        String open = "{\r\n  \"Belege-Gruppe\": [\r\n    {\r\n      \"Signaturzertifikat\": \"\",\r\n      \"Zertifizierungsstellen\": [],\r\n      \"Belege-kompakt\": [";
        String end = "      ]\r\n    }\r\n   ]\r\n}";

        File resultFile = null;
        FileOutputStream resultFileStream = null;
        TestData testData = new TestData(0,
                "",
                null,
                new HashSet<String>(),
                isFristReceiptNotIncluded,
                isFristReceiptNotIncluded,
                cryptoFileLocation,
                new TestResult(outputLocation),
                resultFileStream);

        List<String> firstDepLines = getFirstDepLines(depFileLocation);
        List<String> firstDepLinesOrdered = new ArrayList<>();
        try {
            firstDepLinesOrdered = orderDepLines(firstDepLines);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e1) {
            //TODO: couldn't order DepTests ErrorMessage
        }
        try {
            File depPartFile;
            BufferedWriter depPartFileWriter;
            int forcounter = 0;
            int forcounter2 = 0;
            Receipt receipt;
            boolean firstLineFlag = true;

            for (String element : firstDepLinesOrdered) {

                depPartFile = tmpFactory.getNewJsonTmpFile("depPart", forcounter+1);
                depPartFileWriter = new BufferedWriter(new FileWriter(depPartFile));
                advResult.addDepPartFile(depPartFile);

                resultFile = tmpFactory.getNewTxtTmpFile("depPartStructured", forcounter+1);
                resultFileStream = new FileOutputStream(resultFile);
                testData = new TestData(0,
                        "",
                        null,
                        new HashSet<String>(),
                        isFristReceiptNotIncluded,
                        isFristReceiptNotIncluded,
                        cryptoFileLocation,
                        new TestResult(outputLocation),
                        resultFileStream);
                advResult.addTestData(testData);
                advResult.addDepStructuredFile(resultFile);


                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(depFileLocation)));
                String line;
                int lineNr = 0;
                forcounter2 = 0;
                while ((line = br.readLine()) != null) {
                    if (line.contains("Belege-kompakt") && firstLineFlag) {
                        depPartFileWriter.newLine();
                        firstLineFlag = false;
                    }
                    if (firstLineFlag && line.contains(".")) {
                        receipt = decryptionLogic.DepStringToReceipt(lineNr, line);
                        testData = decryptionLogic.checkAndPrintReceipt(receipt, testData);
                        depPartFileWriter.write(line);
                        depPartFileWriter.write("\r\n");
                        lineNr++;
                    }

                    if (line.contains(element)) {
                        depPartFileWriter.write(open);
                        depPartFileWriter.newLine();
                        for (String element2 : firstDepLinesOrdered) {
                            if (forcounter2 < forcounter) //noinspection MagicConstant
                            {
                                receipt = decryptionLogic.DepStringToReceipt(lineNr, element2);
                                testData = decryptionLogic.checkAndPrintReceipt(receipt, testData);
                                depPartFileWriter.write(element2);
                                depPartFileWriter.newLine();
                                lineNr++;
                            }
                            forcounter2++;
                        }
                        receipt = decryptionLogic.DepStringToReceipt(lineNr, line);
                        testData = decryptionLogic.checkAndPrintReceipt(receipt, testData);
                        depPartFileWriter.write(line);
                        depPartFileWriter.newLine();
                        firstLineFlag = true;
                        lineNr++;
                    }

                }
                forcounter++;
                depPartFileWriter.write(end);
                depPartFileWriter.flush();
                depPartFileWriter.close();
            }
            resultFileStream.close();

        } catch (IOException | NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (runDepTests) {
            try {
                advResult = runDepTestForAllResults(advResult, cryptoFileLocation, validFutureDates, showDetails);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                // TODO fix errorhandling
                e.printStackTrace();
            }
        }
        //TODO check Date check
        printResult(advResult);
        return advResult;
    }

    private AdvResult runDepTestForAllResults(AdvResult advResult, String cryptoFileLocation, boolean validFutureDates, boolean showDetails) throws IOException {
        int filecounter = 0;
        for (File depPartFile : advResult.getDepPartFiles()) {
            File depTestFile = tmpFactory.getNewJsonTmpFile("depPartTest", filecounter+1);
            BufferedWriter depTestFileWriter = new BufferedWriter(new FileWriter(depTestFile));
            advResult.addDepTestFile(depTestFile);

            Runtime runtime = Runtime.getRuntime();
            Process process = null;
            String processString = ioTools.createDepProcessString(depPartFile.getAbsolutePath(), cryptoFileLocation, null, validFutureDates, showDetails);
            process = runtime.exec(processString);
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader brDep = new BufferedReader(isr);
            String lineDep;
            while ((lineDep = brDep.readLine()) != null) {
                depTestFileWriter.write(lineDep + "\r\n");
                if (lineDep.contains("Step 2: RKSV-DEP-EXPORT Validation:")) {
                    process.destroy();
                    depTestFileWriter.write("NOT DONE ! \r\n");
                }

            }
            depTestFileWriter.flush();
            depTestFileWriter.close();
            filecounter++;
        }
        return advResult;
    }


    private void printResult(AdvResult advResult) throws IOException {
        BufferedWriter output = new BufferedWriter(new FileWriter(advResult.getOuputLocation()));
        output.write("Splitting DepFile successful!\r\n");
        output.write("Number of DepFiles found : " + advResult.numberOfDepFilesFound() + "\r\n\r\n");
        output.write("Results:  \r\n");
        for(int i=0;i<advResult.getAllTestData().size();i++){
            output.write("Part: "+(i+1)+"  \r\n");
            output.write(advResult.getSingleTestData(i).testResult.printResults());
            output.write(" \r\n");
        }
        output.flush();
        output.close();
    }

    private List<String> getFirstDepLines(String depFileLocation) throws IOException {
        boolean firstLineFlag = false;

        List<String> firstDepLines = new ArrayList<String>();
        FileInputStream inputStream = new FileInputStream(depFileLocation);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        while ((line = br.readLine()) != null) {
            if (firstLineFlag) {
                firstDepLines.add(line);
                firstLineFlag = false;
            }
            if (line.contains("Belege-kompakt")) {
                firstLineFlag = true;

            }

        }
        inputStream.close();
        return firstDepLines;
    }

    private List<String> orderDepLines(List<String> orderdFirstDepLines) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        List<String> result = new ArrayList<String>();
        String nextSig = "";
        for (String element : orderdFirstDepLines) {
            try {
                if (checkIfFirstDepLine(element)) {
                    result.add(element);
                    nextSig = calcNextSig(element);
                }
            } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (result.size() > 1) {
            nextSig = calcNextSig(result.get(0));
        }
        int whileflag = 0;
        while (whileflag == 0) {
            for (String element : orderdFirstDepLines) {
                try {
                    if (checkDepSigValue(element, nextSig)) {
                        result.add(element);
                        nextSig = calcNextSig(element);
                        whileflag = -1;
                    }
                } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            whileflag++;
        }

        return result;
    }

    private boolean checkIfFirstDepLine(String depLine) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        CryptoTools cryptoTools = new CryptoTools();
        depLine = depLine.substring(depLine.indexOf("\"") + 1);
        depLine = depLine.substring(0, depLine.indexOf("\""));
        String[] parts3 = depLine.split("[.]");
        byte[] parts4 = null;
        if (parts3.length > 1) {
            parts4 = cryptoTools.base64Decode(parts3[1], false);
        } else {
            parts4 = "NOT VALID".getBytes();
        }
        String PartString = new String(parts4, "UTF-8");
        String[] parts2 = PartString.split("_");
        MessageDigest md = MessageDigest.getInstance("sha-256");

        // calculate hash value
        md.update(parts2[2].getBytes());
        byte[] digest = md.digest();
        // extract number of bytes (N, defined in RKsuite)
        // from
        // hash value
        int bytesToExtract = 8;
        byte[] conDigest = new byte[bytesToExtract];
        System.arraycopy(digest, 0, conDigest, 0, bytesToExtract);
        // encode value as BASE64 String ==> chainValue
        byte[] Sig_Vor_Beleg = Base64.encodeBase64(conDigest, false);
        String Sig_Vor_Beleg_String = new String(Sig_Vor_Beleg, "UTF-8");

        if (Sig_Vor_Beleg_String.equals(parts2[parts2.length - 1])) {
            return true;
        } else {
            return false;
        }

    }

    private String calcNextSig(String depLine) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        depLine = depLine.substring(depLine.indexOf("\"") + 1);
        depLine = depLine.substring(0, depLine.indexOf("\""));
        String result = "";
        MessageDigest md1 = MessageDigest.getInstance("sha-256");
        md1.update(depLine.getBytes());
        byte[] digest1 = md1.digest();

        // extract number of bytes (N, defined in RKsuite)
        // from
        // hash value
        int bytesToExtract1 = 8;
        byte[] conDigest1 = new byte[bytesToExtract1];
        System.arraycopy(digest1, 0, conDigest1, 0, bytesToExtract1);

        // encode value as BASE64 String ==> chainValue
        byte[] Sig_Nae_Beleg = Base64.encodeBase64(conDigest1, false);
        result = new String(Sig_Nae_Beleg, "UTF-8");
        return result;

    }

    private boolean checkDepSigValue(String depLine, String sig) throws UnsupportedEncodingException {
        CryptoTools cryptoTools = new CryptoTools();
        depLine = depLine.substring(depLine.indexOf("\"") + 1);
        depLine = depLine.substring(0, depLine.indexOf("\""));
        String[] parts3 = depLine.split("[.]");
        byte[] parts4 = null;
        if (parts3.length > 1) {
            parts4 = cryptoTools.base64Decode(parts3[1], false);
        } else {
            parts4 = "NOT VALID".getBytes();
        }
        String PartString = new String(parts4, "UTF-8");
        String[] parts2 = PartString.split("_");

        if (sig.equals(parts2[parts2.length - 1])) {
            return true;
        } else {
            return false;
        }

    }
}
