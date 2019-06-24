package sample.Util.depLogic;

import sample.Util.Result;

import java.io.FileOutputStream;
import java.util.HashSet;

public class LogicInput {
    public String[] parts;
    public double oldRevenueValue;
    public String oldSignature;
    public String oldDate;
    public HashSet allReceiptIds;
    public boolean errorBlocker;
    public boolean isFristReceiptNotIncluded;
    public String cryptoFileLocation;
    public DepTestResult depTestResult;
    public FileOutputStream resultFile;

    public LogicInput(String[] parts, double oldRevenueValue, String oldSignature, String oldDate, HashSet allReceiptIds, boolean errorBlocker, boolean isFristReceiptNotIncluded, String cryptoFileLocation, DepTestResult depTestResult, FileOutputStream resultFile) {
        this.parts = parts;
        this.oldRevenueValue = oldRevenueValue;
        this.oldSignature = oldSignature;
        this.oldDate = oldDate;
        this.allReceiptIds = allReceiptIds;
        this.errorBlocker = errorBlocker;
        this.isFristReceiptNotIncluded = isFristReceiptNotIncluded;
        this.cryptoFileLocation = cryptoFileLocation;
        this.depTestResult = depTestResult;
        this.resultFile = resultFile;
    }
}
