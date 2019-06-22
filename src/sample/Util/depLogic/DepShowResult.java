package sample.Util.depLogic;

import sample.Util.Result;
import sample.Util.enums.ActionTyp;

import java.io.File;
import java.util.ArrayList;

public class DepShowResult implements Result {

    private File outputLocation;

    public DepShowResult(File outputLocation) {
        this.outputLocation = outputLocation;
    }

    @Override
    public File getOuputLocation() {
        return outputLocation;
    }

    @Override
    public void setOuputLocation(File file) {
        outputLocation=file;
    }
}
