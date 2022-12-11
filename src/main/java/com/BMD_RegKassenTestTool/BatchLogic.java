package com.bmd_regkassentesttool;

import com.bmd_regkassentesttool.Util.Configuration;
import com.bmd_regkassentesttool.Util.DepLogic.AdvDepTestLogic;
import com.bmd_regkassentesttool.Util.DepLogic.DepTestLogic;
import com.bmd_regkassentesttool.Util.DepLogic.Results.AdvResult;
import com.bmd_regkassentesttool.Util.DepLogic.Results.TestResult;
import com.bmd_regkassentesttool.Util.Enums.ResultTyp;
import com.bmd_regkassentesttool.Util.Factories.TmpFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class
BatchLogic {
    AdvDepTestLogic advDepTestLogic;
    TmpFactory tmpFactory;
    DepTestLogic depTestLogic;

    public void start(String[] args) {


        Configuration config = new Configuration();
        String location = System.getProperty("java.io.tmpdir");
        config.setJunkFolder(location);
        this.depTestLogic = new DepTestLogic(config);
        this.advDepTestLogic = new AdvDepTestLogic(config);
        this.tmpFactory = new TmpFactory(config);

        boolean appendFlag = false;
        boolean firstReceiptFlag = false;
        System.out.println("checking parameter...");
        if (args.length > 2) {
            if (fileExists(args[0]) && fileExists(args[1])) {
                if (args.length > 3 && Arrays.asList(args).contains("-a")) {
                    appendFlag = true;
                }
                if (args.length > 3 && Arrays.asList(args).contains("-r")) {
                    firstReceiptFlag = true;
                }
                if (args.length > 3 && Arrays.asList(args).contains("-adv")) {
                    try {
                        int fnp = Arrays.asList(args).indexOf("-fn");
                        String fileName = null;
                        if (fnp != -1 && args.length > (fnp + 1)) {
                            fileName = args[fnp + 1];

                        }
                        executeWithAdv(firstReceiptFlag, appendFlag, args[0], args[1], args[2], fileName);
                    } catch (IOException | ParseException e) {
                        System.out.println("ERROR: Something went wrong during decryption process");
                        e.printStackTrace();
                        System.exit(0);
                    }
                } else {
                    try {
                        executeNormal(firstReceiptFlag, appendFlag, args[0], args[1], args[2]);
                    } catch (IOException | NoSuchAlgorithmException | ParseException e) {
                        System.out.println("ERROR: Something went wrong during decryption process.");
                        e.printStackTrace();
                        System.exit(0);
                    }
                }


            } else {
                System.out.println("ERROR: Can't start the process because input parameter are missing or false.");
            }
        } else {
            System.out.println("ERROR: Can't start the process because input parameter are missing or false.");
        }
        System.out.println("Done!");
        System.exit(0);
    }

    private boolean fileExists(String fileLocation) {
        File file = new File(fileLocation);
        return file.exists();
    }

    private FileOutputStream checkandPrepareOutputFile(String fileLocation, boolean appendFlag) {
        try {
            return new FileOutputStream(checkAndGetOutputFile(fileLocation), appendFlag);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private File checkAndGetOutputFile(String fileLocation) {
        File outputfile = new File(fileLocation);
        try {
            outputfile.createNewFile();
            return outputfile;

        } catch (IOException e) {
            System.out.println("ERROR: Outputfile couldn't be created or was blocked");
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }

    private boolean safeAdvDepResult(AdvResult advResult, File output) {
        String newLocation = output.getParent();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Date date = new Date(System.currentTimeMillis());
        String dateString = formatter.format(date);
        int i = 1;
        for (File fileToMove : advResult.getDepPartFiles()) {
            File newfile = new File(newLocation + "\\" + "DepPartFile_" + i + "_" + dateString + ".json");
            fileToMove.renameTo(newfile);
            i++;
        }
        i = 1;
        for (File fileToMove : advResult.getDepStructuredFile()) {
            File newfile = new File(newLocation + "\\" + "DepStructuredFile_" + i + "_" + dateString + ".txt");
            fileToMove.renameTo(newfile);
            i++;
        }
        i = 1;
        for (File fileToMove : advResult.getDepTestFiles()) {
            File newfile = new File(newLocation + "\\" + "DepTestFile_" + i + "_" + dateString + ".json");
            fileToMove.renameTo(newfile);
            i++;
        }
        return true;
    }

    private boolean safeAdvDepResult(AdvResult advResult, File output, String fileName) {
        String newLocation = output.getParent();
        int i = 1;
        for (File fileToMove : advResult.getDepPartFiles()) {
            File newfile = new File(newLocation + "\\" + fileName + "dep" + i + ".json");
            fileToMove.renameTo(newfile);
            i++;
        }
        i = 1;
        for (File fileToMove : advResult.getDepStructuredFile()) {
            File newfile = new File(newLocation + "\\" + fileName + "dep" + i + ".txt");
            fileToMove.renameTo(newfile);
            i++;
        }
        i = 1;
        for (File fileToMove : advResult.getDepTestFiles()) {
            File newfile = new File(newLocation + "\\" + "fileName" + "_test" + i + ".json");
            fileToMove.renameTo(newfile);
            i++;
        }
        return true;
    }

    private void executeWithAdv(boolean firstReceiptFlag, boolean appendFlag, String depFileLocation, String cryptoFileLocation, String outputFileLocation, String fileName) throws IOException, ParseException {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();

        System.out.println("Test on " + dateFormat.format(date));

        FileOutputStream outputFileStream;
        outputFileStream = checkandPrepareOutputFile(outputFileLocation, appendFlag);

        File outputFile = checkAndGetOutputFile(outputFileLocation);
        AdvResult advResult = advDepTestLogic.runAdvDepTest(depFileLocation, cryptoFileLocation, firstReceiptFlag, outputFile, false, false, false, false);
        if (fileName != null) {
            safeAdvDepResult(advResult, outputFile, fileName);
        } else {
            safeAdvDepResult(advResult, outputFile);
        }

        outputFileStream.write("Test on ".getBytes());
        outputFileStream.write(dateFormat.format(date).getBytes());
        outputFileStream.write("\r\n".getBytes());
        outputFileStream.write(advResult.printTestData().getBytes());
        outputFileStream.write("\r\n".getBytes());
        outputFileStream.close();

        System.out.println(advResult.printTestData());
    }

    private void executeNormal(boolean firstReceiptFlag, boolean appendFlag, String depFileLocation, String cryptoFileLocation, String outputFileLocation) throws IOException, ParseException, NoSuchAlgorithmException {
        FileOutputStream outputFileStream;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        outputFileStream = checkandPrepareOutputFile(outputFileLocation, appendFlag);

        File tmpFile = tmpFactory.getNewTmpFile(ResultTyp.SHOWDEPFILE);
        TestResult testResult = depTestLogic.decryptAndStructureDepFile(depFileLocation, cryptoFileLocation, firstReceiptFlag, tmpFile);
        System.out.println(testResult.printResults());

        outputFileStream.write("Test on ".getBytes());
        outputFileStream.write(dateFormat.format(date).getBytes());
        outputFileStream.write("\r\n".getBytes());
        outputFileStream.write(testResult.printResults().getBytes());
        outputFileStream.write("\r\n".getBytes());
        outputFileStream.close();
    }
}
