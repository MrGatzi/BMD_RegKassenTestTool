package sample.Util;

import java.util.ArrayList;
import java.util.List;

public class DepTestResult {

    StringBuilder ouuputString = new StringBuilder();
    private List<Integer> wrongChainedReceipts;
    private int properChainedReceipts;
    private List<Integer> wrongRevenueSet;
    private int properRevenueSet;
    private List<Integer> wrongSetValues;
    private List<Integer> wrongStructureValues;
    private List<Integer> wrongDates;
    private List<Integer> wrongChainedDates;
    private List<Integer> wrongReceiptId;

    public DepTestResult() {
        wrongChainedReceipts = new ArrayList<Integer>();
        properChainedReceipts = 0;
        wrongRevenueSet = new ArrayList<Integer>();
        properRevenueSet = 0;
        wrongSetValues = new ArrayList<Integer>();
        wrongStructureValues = new ArrayList<Integer>();
        wrongDates = new ArrayList<Integer>();
        wrongChainedDates = new ArrayList<Integer>();
        wrongReceiptId = new ArrayList<Integer>();
    }

    public void addChainedReceipt(int receiptId, boolean proper) {
        if (proper) {
            properChainedReceipts++;
        } else {
            wrongChainedReceipts.add(receiptId);
        }
    }

    public void addRevenueSet(int receiptId, boolean proper) {
        if (proper) {
            properRevenueSet++;
        } else {
            wrongRevenueSet.add(receiptId);
        }
    }

    public void addWrongSetValue(int receiptId) {
        wrongSetValues.add(receiptId);
    }

    public void addWrongStructureValues(int receiptId) {
        wrongStructureValues.add(receiptId);
    }

    public void addWrongDate(int receiptId) {
        wrongDates.add(receiptId);
    }

    public void addWrongChainedDate(int receiptId) {
        wrongChainedDates.add(receiptId);
    }

    public void addWrongReceiptId(int receiptId) {
        wrongReceiptId.add(receiptId);
    }

    public String printResults() {
        ouuputString = new StringBuilder();
        ouuputString.append("Listen Elemente: " + (wrongChainedReceipts.size() + properChainedReceipts) + " , davon falsch verkettet: " + wrongChainedReceipts.size() + " \r\n");
        addReceiptIdOfWrongValues(wrongChainedReceipts);
        ouuputString.append("Berechnete Umsatzzähler: " + (properRevenueSet + wrongRevenueSet.size()) + " , davon falsch verkettet: " + wrongRevenueSet.size() + " \r\n");
        addReceiptIdOfWrongValues(wrongRevenueSet);
        ouuputString.append("Belege mit falschen Betragsspalten: " + wrongSetValues.size() + " \r\n");
        addReceiptIdOfWrongValues(wrongSetValues);
        ouuputString.append("Belege mit falschem Aufbau: " + wrongStructureValues.size() + " \r\n");
        addReceiptIdOfWrongValues(wrongStructureValues);
        ouuputString.append("Belege mit falscher BelegID: " + wrongReceiptId.size() + " \r\n");
        addReceiptIdOfWrongValues(wrongReceiptId);
        ouuputString.append("Belege mit falschem Datum: " + wrongDates.size() + " \r\n");
        addReceiptIdOfWrongValues(wrongDates);
        ouuputString.append("Belege mit falscher Datumsverkettung: " + wrongChainedDates.size() + " \r\n");
        addReceiptIdOfWrongValues(wrongChainedDates);

        return ouuputString.toString();
    }

    //TODO CHECK IF NEEDED
    public void addReceiptIdOfWrongValues(List<Integer> input) {
        if (input.size() > 0) {
            ouuputString.append("Folgende Belege sind betroffen: ");
            ouuputString.append(input);
            ouuputString.append("\r\n");
        }
    }
}
