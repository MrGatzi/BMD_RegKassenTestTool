package sample;

import sample.Util.Configuration;
import sample.Util.DepLogic.DepTestLogic;
import sample.Util.DepLogic.Results.TestResult;
import sample.Util.Enums.ResultTyp;
import sample.Util.Factories.TmpFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

public class BatchLogic {
    public void start(String[] args) {


            Configuration config = new Configuration();
            String location=System.getProperty("java.io.tmpdir");
            config.setJunkFolder(location);
            DepTestLogic depTestLogic = new DepTestLogic(new Configuration());
            TmpFactory tmpFactory = new TmpFactory(config);

            boolean appendFlag = false;
            boolean firstReceiptFlag = false;
            FileOutputStream outputFile;

            if (args.length > 2) {
                if (fileExists(args[0]) && fileExists(args[1])) {
                    if (args.length > 3 && args[3].equals("-a")) {
                        appendFlag = true;
                    }
                    outputFile = checkandPrepareOutputFile(args[2], appendFlag);
                    if ((args.length > 3 && args[3].equals("-r")) || (args.length > 4 && args[4].equals("-r"))) {
                        firstReceiptFlag = true;
                    }
                    try {
                        File tmpFile = tmpFactory.getNewTmpFile(ResultTyp.SHOWDEPFILE);
                        TestResult testResult = depTestLogic.decryptAndStructureDepFile(args[0], args[1], firstReceiptFlag, tmpFile);
                        System.out.println(testResult.printResults());
                        outputFile.write(testResult.printResults().getBytes());
                        outputFile.write("\r\n".getBytes());
                        outputFile.close();
                    } catch (IOException | NoSuchAlgorithmException | ParseException e) {
                        System.out.println("ERROR: Something went wrong during decryption process");
                        e.printStackTrace();
                        System.exit(0);
                    }
                }else{
                    System.out.println("ERROR: Can't start the process because input parameter are missing or false.");
                }
            } else {
                System.out.println("ERROR: Can't start the process because input parameter are missing or false.");
            }
            System.exit(0);
    }

    private boolean fileExists(String fileLocation) {
        File file = new File(fileLocation);
        if (file.exists()){
            return true;
        }
        return false;
    }

    private FileOutputStream checkandPrepareOutputFile(String fileLocation, boolean appendFlag) {
        File outputfile= new File(fileLocation);
        try {
            outputfile.createNewFile();
            return new FileOutputStream(fileLocation, appendFlag);

        } catch (IOException e) {
            System.out.println("ERROR: Outputfile couldn't be created or was blocked");
            e.printStackTrace();
            System.exit(0);
        }

        return null;
    }
}
