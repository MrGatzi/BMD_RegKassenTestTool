package sample.Util.factories;

import sample.Util.Configuration;
import sample.Util.enums.ResultTyp;

import java.io.File;
import java.io.IOException;


public class TmpFactory {

    Configuration config;

    public TmpFactory(Configuration config) {
        this.config = config;
    }

    public File getNewTmpFile(ResultTyp typ) throws IOException {
        File junkFolder = new File(config.getJunkFolder());

        if (!junkFolder.exists()) {
            junkFolder.mkdir();
        }
        String name = "";
        switch (typ) {
            case RUNDEPTEST:
                name = "DepTestResult";
                break;
            case SHOWDEPFILE:
                name = "DepFile";
                break;
            case FILTERDEPTEST:
                name = "FilterResult";
                break;
        }
        File tempFile = File.createTempFile(name, ".tmp.txt", junkFolder);
        tempFile.deleteOnExit();
        return tempFile;
    }

}
