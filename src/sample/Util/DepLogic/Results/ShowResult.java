package sample.Util.DepLogic.Results;

import sample.Util.Result;

import java.io.File;

public class ShowResult implements Result {

    private File outputLocation;

    public ShowResult(File outputLocation) {
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
