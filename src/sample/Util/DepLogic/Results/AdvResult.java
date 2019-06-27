package sample.Util.DepLogic.Results;

import sample.Util.DepLogic.TestData;
import sample.Util.Result;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdvResult implements Result {
    private File outputLocation;
    private List<File> depPartFiles;
    private List<File> depStructuredFiles;
    private List<File> depTestFiles;
    TestData testData;


    public AdvResult(File outputLocation) {
        this.outputLocation = outputLocation;
        depPartFiles = new ArrayList<File>();
        depStructuredFiles = new ArrayList<File>();
        depTestFiles = new ArrayList<File>();
        testData=null;
    }

    @Override
    public File getOuputLocation() {
        return outputLocation;
    }

    @Override
    public void setOuputLocation(File file) {
        outputLocation = file;
    }

    public void addPartFile(File file){
        depPartFiles.add(file);
    }
    public void adddepStructuredFile(File file){
        depStructuredFiles.add(file);
    }
    public void adddepTestFile(File file){
        depTestFiles.add(file);
    }
    public TestData getTestData() {
        return testData;
    }

    public void setTestData(TestData testData) {
        this.testData = testData;
    }

}