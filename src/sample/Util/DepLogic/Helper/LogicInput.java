package sample.Util.DepLogic.Helper;

import sample.Util.Receipt;
import sample.Util.DepLogic.Results.TestResult;

import java.io.FileOutputStream;
import java.util.HashSet;

public class LogicInput {
    public Receipt[] parts;
    public double oldRevenueValue;
    public String oldSignature;
    public String oldDate;
    public HashSet allReceiptIds;
    public boolean errorBlocker;
    public boolean isFristReceiptNotIncluded;
    public String cryptoFileLocation;
    public TestResult testResult;
    public FileOutputStream resultFile;

    public LogicInput(Receipt[] parts, double oldRevenueValue, String oldSignature, String oldDate, HashSet allReceiptIds, boolean errorBlocker, boolean isFristReceiptNotIncluded, String cryptoFileLocation, TestResult testResult, FileOutputStream resultFile) {
        this.parts = parts;
        this.oldRevenueValue = oldRevenueValue;
        this.oldSignature = oldSignature;
        this.oldDate = oldDate;
        this.allReceiptIds = allReceiptIds;
        this.errorBlocker = errorBlocker;
        this.isFristReceiptNotIncluded = isFristReceiptNotIncluded;
        this.cryptoFileLocation = cryptoFileLocation;
        this.testResult = testResult;
        this.resultFile = resultFile;
    }
}
