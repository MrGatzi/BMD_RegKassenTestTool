package com.bmd_regkassentesttool.Util.DepLogic.Results;

import com.bmd_regkassentesttool.Util.DepLogic.TestData;
import com.bmd_regkassentesttool.Util.Result;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdvResult implements Result {
    private File outputLocation;
    private List<File> depPartFiles;
    private List<File> depStructuredFiles;
    private List<File> depTestFiles;
    private List<TestData> testData;


    public AdvResult(File outputLocation) {
        this.outputLocation = outputLocation;
        depPartFiles = new ArrayList<File>();
        depStructuredFiles = new ArrayList<File>();
        depTestFiles = new ArrayList<File>();
        testData = new ArrayList<TestData>();
    }

    @Override
    public File getOuputLocation() {
        return outputLocation;
    }

    @Override
    public void setOuputLocation(File file) {
        outputLocation = file;
    }

    public int numberOfDepFilesFound() {
        return depPartFiles.size();
    }

    public List<File> getDepPartFiles() {
        return depPartFiles;
    }

    public List<File> getDepStructuredFile() {
        return depStructuredFiles;
    }

    public List<File> getDepTestFiles() {
        return depTestFiles;
    }

    public void addDepPartFile(File file) {
        depPartFiles.add(file);
    }

    public void addDepStructuredFile(File file) {
        depStructuredFiles.add(file);
    }

    public void addDepTestFile(File file) {
        depTestFiles.add(file);
    }

    public List<TestData> getAllTestData() {
        return testData;
    }

    public TestData getSingleTestData(int index) {
        return testData.get(index);
    }

    public void addTestData(TestData testData) {
        this.testData.add(testData);
    }

    public String printTestData(){
        StringBuilder outputString = new StringBuilder();
        outputString.append("Number of DepFiles found : " + numberOfDepFilesFound() + "\r\n\r\n");
        outputString.append("Results:  \r\n");
        for(int i=0;i<getAllTestData().size();i++){
            outputString.append("Part: "+(i+1)+"  \r\n");
            outputString.append(getSingleTestData(i).testResult.printResults());
            outputString.append(" \r\n");
        }
        return outputString.toString();
    }

}
