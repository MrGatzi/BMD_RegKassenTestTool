package sample.Util.depLogic;

import sample.Util.Result;

import java.io.File;

public class FilterResult implements Result {
    int checkedReceipt = 0;
    int errorsFound = 0;
    StringBuilder resultStringBuilder;
    File outputLocation;

    public FilterResult() {
        this.checkedReceipt = 0;
        this.errorsFound = 0;
        this.resultStringBuilder = new StringBuilder();
        this.outputLocation = null;
    }

    public FilterResult(int checkedReceipt, int errorsFound, String resultString) {
        this.checkedReceipt = checkedReceipt;
        this.errorsFound = errorsFound;
        this.resultStringBuilder = new StringBuilder();
        this.resultStringBuilder.append(resultString);
        this.outputLocation = null;
    }

    public FilterResult(int checkedReceipt, int errorsFound, String resultString, File outputLocation) {
        this.checkedReceipt = checkedReceipt;
        this.errorsFound = errorsFound;
        this.resultStringBuilder = new StringBuilder();
        this.resultStringBuilder.append(resultString);
        this.outputLocation = outputLocation;
    }

    public void add(FilterResult partFilterResult) {
        this.checkedReceipt += partFilterResult.checkedReceipt;
        this.errorsFound += partFilterResult.errorsFound;
        resultStringBuilder.append(partFilterResult.getResultString());
    }

    public void addToResultString(String toAppend) {
        resultStringBuilder.append(toAppend);
    }

    public int getCheckedReceipt() {
        return this.checkedReceipt;
    }

    public int getErrorsFound() {
        return this.errorsFound;
    }

    public String getResultString() {
        return resultStringBuilder.toString();
    }

    @Override
    public File getOuputLocation() {
        return outputLocation;
    }

    @Override
    public void setOuputLocation(File file) {
        outputLocation = file;
    }
}
