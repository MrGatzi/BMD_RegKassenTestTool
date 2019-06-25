package sample.Util.DepLogic;

import org.apache.commons.codec.binary.Base64;
import sample.Util.Configuration;
import sample.Util.CryptoTools;
import sample.Util.DepLogic.Results.TestResult;
import sample.Util.Enums.ResultTyp;
import sample.Util.Factories.TmpFactory;
import sample.Util.Receipt;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class AdvDepTestLogic {
    Configuration config;
    TmpFactory tmpFactory;
    DecryptionLogic decryptionLogic;

    public AdvDepTestLogic(Configuration config) {
        this.config = config;
        this.decryptionLogic = new DecryptionLogic();
        this.tmpFactory = new TmpFactory(config);
    }

    public void runWithDEP(String depFileLocation,String cryptoFileLocation,boolean isFristReceiptNotIncluded) throws IOException, ParseException {
        String open = "{\r\n  \"Belege-Gruppe\": [\r\n    {\r\n      \"Signaturzertifikat\": \"\",\r\n      \"Zertifizierungsstellen\": [],\r\n      \"Belege-kompakt\": [";
        String end = "      ]\r\n    }\r\n   ]\r\n}";
        String linebefore = "";


        FileOutputStream resultFile = new FileOutputStream("");
        TestData testData = new TestData(0,
                "",
                null,
                new HashSet<String>(),
                isFristReceiptNotIncluded,
                isFristReceiptNotIncluded,
                cryptoFileLocation,
                new TestResult(new File("")),
                resultFile);


        List<String> firstDepLines = getFirstDepLines(depFileLocation);
        List<String> firstDepLinesOrdered = new ArrayList<>();
        try {
            firstDepLinesOrdered = orderDepLines(firstDepLines);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e1) {
            //TODO: couldn't order DepTests ErrorMessage
        }
        try {
            File file;
            BufferedWriter writer;
            File file2;
            BufferedWriter writer2;
            int forcounter = 0;
            int forcounter2 = 0;
            Receipt receipt;
            boolean firstLineFlag = true;
            for (String element : firstDepLinesOrdered) {
                //TODO: create new Tmp Files?
                file =tmpFactory.getNewJsonTmpFile(ResultTyp.ADVDEPTEST);
                writer = new BufferedWriter(new FileWriter(file));
                file2 =tmpFactory.getNewJsonTmpFile(ResultTyp.ADVDEPTEST);
                writer2 = new BufferedWriter(new FileWriter(file2));
                forcounter2 = 0;

                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(depFileLocation)));
                String line;
                int lineNr=0;
                while ((line = br.readLine()) != null) {
                    if (line.contains("Belege-kompakt") && firstLineFlag) {
                        writer.newLine();
                        firstLineFlag = false;
                    }
                    if (firstLineFlag && line.contains(".")) {
                        lineNr++;
                        receipt=decryptionLogic.DepStringToReceipt(lineNr,line);
                        writer2.write(receipt.toString());
                        testData = decryptionLogic.checkReceipt(receipt, testData);
                        linebefore = line;
                        writer.write(line);
                        writer.newLine();
                    }

                    if (line.contains(element)) {
                        writer.write(open);
                        writer.newLine();
                        for (String element2 : firstDepLinesOrdered) {
                            if (forcounter2 < forcounter) {
                                lineNr++;
                                receipt=decryptionLogic.DepStringToReceipt(lineNr,line);
                                writer2.write(receipt.toString());
                                testData = decryptionLogic.checkReceipt(receipt, testData);
                                linebefore = element2;
                                writer.write(element2);
                                writer.newLine();
                            }
                            forcounter2++;
                        }
                        lineNr++;
                        receipt=decryptionLogic.DepStringToReceipt(lineNr,line);
                        writer2.write(receipt.toString());
                        testData = decryptionLogic.checkReceipt(receipt, testData);
                        linebefore = line;
                        writer.write(line);
                        writer.newLine();
                        firstLineFlag = true;
                    }

                }
                writer2.write(testData.testResult.printResults());
                forcounter++;
                writer.write(end);
                writer.flush();
                writer.close();
                writer2.flush();
                writer2.close();

            }

        } catch (IOException | NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        /*try {

            File fileDep;

            int forcounterDep = 0;
            BufferedWriter writerDep;
            for (String element1 : firstDepLinesOrdered) {

                forcounterDep++;
                fileDep = new File(OutFolder + "/_DepTest_" + (forcounterDep) + ".json");
                fileDep.createNewFile();
                writerDep = new BufferedWriter(new FileWriter(fileDep));
                Runtime runtime = Runtime.getRuntime();
                Process process = null;
                process = runtime.exec("java -jar regkassen-verification-depformat-1.1.1.jar -f -i " + OutFolder + "/_Dep_" + (forcounterDep) + ".json" + " -c " + CryptoFile + " -o " + OutFolder + "/out");
                InputStream is = process.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader brDep = new BufferedReader(isr);
                String lineDep;
                while ((lineDep = brDep.readLine()) != null) {
                    writerDep.write(lineDep + "\r\n");
                    System.out.println(lineDep);
                    if (lineDep.contains("Step 2: RKSV-DEP-EXPORT Validation:")) {
                        process.destroy();
                        writerDep.write("NOT DONE ! \r\n");
                        System.out.println("stopped Process on Step 2 !");
                    }

                }
                writerDep.flush();
                writerDep.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
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
