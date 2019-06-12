package sample.Util;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class IOTools {
    private String defaultTool = "regkassen-verification-depformat-1.1.1.jar";
    private Configuration config;

    public IOTools(Configuration config) {
        this.config = config;
    }

    // Universale funktion um einen Text aus einem angebenen File zu lesen. (Input File-Path)
    public String readTxtFile(String File) throws IOException {
        String Output = "";
        FileInputStream inputStream = new FileInputStream(File);
        try {
            Output = IOUtils.toString(inputStream);
        } finally {
            inputStream.close();
        }
        return Output;
    }

    public String createDepProcessString(String depFileLocation, String cryptoFileLocation, String outputFileLocation, boolean futureReceiptValid, boolean printDetails) {
        StringBuilder depProcessString = new StringBuilder();
        //TODO: change to use CONFIG!
        depProcessString.append("java -Xmx");
        depProcessString.append(config.getRamInput());
        depProcessString.append("m -jar ");
        if (config.isUseDefaultDepTool()) {
            depProcessString.append(defaultTool);
        } else {
            depProcessString.append(config.getExternalDepToolLocation());
        }


        if (futureReceiptValid) {
            depProcessString.append(" -f");
        }
        if (printDetails) {
            depProcessString.append(" -v");
        }
        depProcessString.append(" -i ");
        depProcessString.append(depFileLocation);
        depProcessString.append(" -c ");
        depProcessString.append(cryptoFileLocation);
        depProcessString.append(" -o ");
        if (outputFileLocation != null) {
            File file = new File(outputFileLocation);
            if (file.isDirectory()) {
                depProcessString.append(outputFileLocation);
            } else {
                depProcessString.append("OutputFiles");
            }
        } else {
            //TODO: add ERROR HAndling + tmp Files
        }
        return depProcessString.toString();
    }
}
