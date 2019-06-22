package sample.Util;

public class PartFilterResult {
    int checkedReceipt = 0;
    int errorsFound = 0;
    StringBuilder resultStringBuilder;

    public PartFilterResult() {
        this.checkedReceipt = 0;
        this.errorsFound = 0;
        resultStringBuilder = new StringBuilder();
    }

    public PartFilterResult(int checkedReceipt, int errorsFound, String resultString) {
        this.checkedReceipt = checkedReceipt;
        this.errorsFound = errorsFound;
        resultStringBuilder = new StringBuilder();
        resultStringBuilder.append(resultString);
    }

    public void add(PartFilterResult partFilterResult) {
        this.checkedReceipt += partFilterResult.checkedReceipt;
        this.errorsFound += partFilterResult.errorsFound;
        resultStringBuilder.append(partFilterResult.getResultString());
    }

    public void addToResultString(String toAppend) {
        resultStringBuilder.append(toAppend);
    }

    public String getResultString() {
        return resultStringBuilder.toString();
    }
}
