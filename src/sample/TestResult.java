package sample;

public class TestResult {
    private String outputString;
    private boolean datePatternTest;
    private boolean dateChainedTest;
    private boolean structureTest;
    private boolean idTest;
    private boolean chainedTest;
    private boolean salesCounterTest;

    public TestResult() {
        this.setOutputString("");
        this.datePatternTest = false;
        this.dateChainedTest = true;
        this.structureTest = false;
        this.idTest = false;
        this.chainedTest = false;
        this.salesCounterTest = false;
    }


    public String getOutputString() {
        return outputString;
    }

    public void setOutputString(String outputString) {
        this.outputString = outputString;
    }

    public boolean isDatePatternTest() {
        return datePatternTest;
    }

    public void setDatePatternTest(boolean datePatternTest) {
        this.datePatternTest = datePatternTest;
    }

    public boolean isDateChainedTest() {
        return dateChainedTest;
    }

    public void setDateChainedTest(boolean dateChainedTest) {
        this.dateChainedTest = dateChainedTest;
    }

    public boolean isStructureTest() {
        return structureTest;
    }

    public void setStructureTest(boolean structureTest) {
        this.structureTest = structureTest;
    }

    public boolean isIdTest() {
        return idTest;
    }

    public void setIdTest(boolean idTest) {
        this.idTest = idTest;
    }

    public boolean isChainedTest() {
        return chainedTest;
    }

    public void setChainedTest(boolean chainedTest) {
        this.chainedTest = chainedTest;
    }

    public boolean isSalesCounterTest() {
        return salesCounterTest;
    }

    public void setSalesCounterTest(boolean salesCounterTest) {
        this.salesCounterTest = salesCounterTest;
    }
}
