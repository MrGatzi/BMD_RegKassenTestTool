package sample.Util.depLogic;

import java.io.FileOutputStream;
import java.util.HashSet;

public class LogicOutput {
    public double oldRevenueValue;
    public String oldSignature;
    public String oldDate;
    public HashSet allReceiptIds;
    public boolean errorBlocker;
    public boolean isFristReceiptNotIncluded;
    public DepTestResult depTestResult;

    public LogicOutput(double oldRevenueValue, String oldSignature, String oldDate, HashSet allReceiptIds, boolean errorBlocker, boolean isFristReceiptNotIncluded, DepTestResult depTestResult) {
        this.oldRevenueValue = oldRevenueValue;
        this.oldSignature = oldSignature;
        this.oldDate = oldDate;
        this.allReceiptIds = allReceiptIds;
        this.errorBlocker = errorBlocker;
        this.isFristReceiptNotIncluded = isFristReceiptNotIncluded;
        this.depTestResult = depTestResult;
    }
}
