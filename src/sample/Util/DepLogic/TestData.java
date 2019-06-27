package sample.Util.DepLogic;

import sample.Util.Receipt;
import sample.Util.DepLogic.Results.TestResult;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;

public class TestData {
    public double oldRevenueValue;
    public String oldSignature;
    public String oldDate;
    public HashSet allReceiptIds;
    public boolean errorBlocker;
    public boolean isFristReceiptNotIncluded;
    public String cryptoFileLocation;
    public TestResult testResult;
    public FileOutputStream resultFile;

    public TestData(){

    }

    public TestData(double oldRevenueValue, String oldSignature, String oldDate, HashSet allReceiptIds, boolean errorBlocker, boolean isFristReceiptNotIncluded, String cryptoFileLocation, TestResult testResult, FileOutputStream resultFile) {
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

    public void switchOutputStream(FileOutputStream resultFile) throws IOException {
        if(this.resultFile!=null) {
            this.resultFile.close();
        }
        this.resultFile = resultFile;
    }
}
