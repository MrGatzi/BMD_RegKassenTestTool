package sample.Util;

import sample.Util.enums.ActionTyp;

import java.io.File;
import java.io.IOException;

import static sample.Util.enums.ActionTyp.RUNDEPTEST;


public class TmpFactory {

    Configuration config;

    public TmpFactory(Configuration config) {
        this.config = config;
    }

    public File getNewTmpFile(ActionTyp typ) throws IOException {
        File junkFolder = new File(config.getJunkFolder());

        if (!junkFolder.exists()) {
            junkFolder.mkdir();
        }
        String name="";
        switch (typ) {
            case RUNDEPTEST:
                name="DepTestResult";
                break;
            case SHOWDEPFILE:
                name="DepFile";
                break;
        }
        File tempFile = File.createTempFile(name, ".txt", junkFolder);
        tempFile.deleteOnExit();
        return tempFile;
    }

}
