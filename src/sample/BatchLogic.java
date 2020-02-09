package sample;

import sample.Util.Configuration;
import sample.Util.DepLogic.AdvDepTestLogic;
import sample.Util.DepLogic.DepTestLogic;
import sample.Util.DepLogic.Results.AdvResult;
import sample.Util.DepLogic.Results.TestResult;
import sample.Util.Enums.ResultTyp;
import sample.Util.Factories.TmpFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BatchLogic {
    public void start(String[] args) {


        Configuration config = new Configuration();
        String location = System.getProperty("java.io.tmpdir");
        config.setJunkFolder(location);
        DepTestLogic depTestLogic = new DepTestLogic(config);
        AdvDepTestLogic advDepTestLogic = new AdvDepTestLogic(config);
        TmpFactory tmpFactory = new TmpFactory(config);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        System.out.println("Test on "+dateFormat.format(date));

        boolean appendFlag = false;
        boolean firstReceiptFlag = false;
        FileOutputStream outputFileStream;

        if (args.length > 2) {
            if (fileExists(args[0]) && fileExists(args[1])) {
                if (args.length > 3 && args[3].equals("-a")) {
                    appendFlag = true;
                }
                if ((args.length > 3 && args[3].equals("-r")) || (args.length > 4 && args[4].equals("-r"))) {
                    firstReceiptFlag = true;
                }
                if((args.length > 3 && args[3].equals("-adv")) || (args.length > 4 && args[4].equals("-adv"))|| (args.length > 5 && args[5].equals("-adv"))){
                    try {
                        outputFileStream = checkandPrepareOutputFile(args[2], appendFlag);

                        File outputFile = checkAndGetOutputFile(args[2]);
                        AdvResult advResult = advDepTestLogic.runAdvDepTest(args[0], args[1], firstReceiptFlag, outputFile, false, false, false,false);
                        safeAdvDepResult(advResult, outputFile);

                        outputFileStream.write("Test on ".getBytes());
                        outputFileStream.write(dateFormat.format(date).getBytes());
                        outputFileStream.write("\r\n".getBytes());
                        outputFileStream.write(advResult.printTestData().getBytes());
                        outputFileStream.write("\r\n".getBytes());
                        outputFileStream.close();

                        System.out.println(advResult.printTestData());
                    } catch (IOException | ParseException e) {
                        System.out.println("ERROR: Something went wrong during decryption process");
                        e.printStackTrace();
                        System.exit(0);
                    }
                }else{
                    try {
                        outputFileStream = checkandPrepareOutputFile(args[2], appendFlag);

                        File tmpFile = tmpFactory.getNewTmpFile(ResultTyp.SHOWDEPFILE);
                        TestResult testResult = depTestLogic.decryptAndStructureDepFile(args[0], args[1], firstReceiptFlag, tmpFile);
                        System.out.println(testResult.printResults());

                        outputFileStream.write("Test on ".getBytes());
                        outputFileStream.write(dateFormat.format(date).getBytes());
                        outputFileStream.write("\r\n".getBytes());
                        outputFileStream.write(testResult.printResults().getBytes());
                        outputFileStream.write("\r\n".getBytes());
                        outputFileStream.close();
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
        if (file.exists()) {
            return true;
        }
        return false;
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
}
