package sample.Util;

import sample.logic.__Coding;

import java.io.IOException;
import java.math.BigInteger;

public class Receipt {
    String[] QR_Code_Titels = {"", "ZDA: ", "Kassen-ID:", "Belegnummer:", "Beleg-Datum-Uhrzeit:",
            "Betrag-Satz-Normal:", "Betrag-Satz-Ermaessigt-1:", "Betrag-Satz-Ermaessigt-2:", "Betrag-Satz-Null:",
            "Betrag-Satz-Besonders:", "Stand-Umsatz-Zaehler-AES256-ICM_Entschlüsselt:", "Zertifikat-Seriennummer:",
            "Sig-Voriger-Beleg:", "Signatur:", "", "", ""}; // Array mit die vor einem Wert bei der "ShowDEP" und "schowQR" methode angezeigt werden

    private int receiptNumber;
    private String zda;
    private String registerId;
    private String receiptId;
    private String receiptDate;
    private String receiptSetNormal;
    private String receiptSetReduced1;
    private String receiptSetReduced2;
    private String receiptSetNull;
    private String receiptSetSpecial;
    private String revenueDecrypted;
    private String revenueEncrypted;
    private String revenueShouldBe;
    private String certificateNumber;
    private String signaturePreviousValue;
    private String signatureNextValue;
    private String signature;

    private double revenueShouldBeNumber;
    private double revenueDecryptedNumber;
    private double revenueEncryptedNumber;

    private double receiptSetNormalNumber;
    private double receiptSetReduced1Number;
    private double receiptSetReduced2Number;
    private double receiptSetNullNumber;
    private double receiptSetSpecialNumber;

    private boolean receiptProperEncrypted;

    __Coding code = new __Coding();

    public Receipt(){
        this.receiptNumber=receiptNumber;
        zda=null;
        registerId=null;
        receiptId =null;
        receiptDate=null;
        receiptSetNormal=null;
        receiptSetReduced1=null;
        receiptSetReduced2=null;
        receiptSetNull=null;
        receiptSetSpecial=null;
        revenueEncrypted=null;
        revenueDecrypted=null;
        revenueShouldBe=null;
        certificateNumber=null;
        signaturePreviousValue =null;
        signature =null;
        receiptProperEncrypted =false;
    }

    public String toString() {
        StringBuilder receiptString = new StringBuilder();

        receiptString.append("Beleg: ");
        receiptString.append(receiptNumber);
        receiptString.append("\r\n");

        receiptString.append("ZDA: ");
        receiptString.append(zda);
        receiptString.append("\r\n");

        receiptString.append("Kassen-ID: ");
        receiptString.append(registerId);
        receiptString.append("\r\n");

        receiptString.append("Belegnummer: ");
        receiptString.append(receiptId);
        receiptString.append("\r\n");

        receiptString.append("Beleg-Datum-Uhrzeit: ");
        receiptString.append(receiptDate);
        receiptString.append("\r\n");

        receiptString.append("Betrag-Satz-Normal: ");
        receiptString.append(receiptSetNormal);
        receiptString.append("\r\n");

        receiptString.append("Betrag-Satz-Ermaessigt-1: ");
        receiptString.append(receiptSetReduced1);
        receiptString.append("\r\n");

        receiptString.append("Betrag-Satz-Ermaessigt-2: ");
        receiptString.append(receiptSetReduced2);
        receiptString.append("\r\n");

        receiptString.append("Betrag-Satz-Null: ");
        receiptString.append(receiptSetNull);
        receiptString.append("\r\n");

        receiptString.append("Betrag-Satz-Besonders: ");
        receiptString.append(receiptSetSpecial);
        receiptString.append("\r\n");

        receiptString.append("Stand-Umsatz-Zaehler-AES256-ICM_Verschlüsselt: ");
        receiptString.append(revenueDecrypted);
        receiptString.append("\r\n");

        receiptString.append("tand-Umsatz-Zaehler-AES256-ICM_Entschlüsselt: ");
        receiptString.append(revenueEncrypted);
        receiptString.append("\r\n");

        if(revenueShouldBe!=null) {
            receiptString.append("Stand-Umsatz-Zaehler_Sollsumme: ");
            receiptString.append(revenueShouldBe);
            receiptString.append("\r\n");
        }

        receiptString.append("Zertifikat-Seriennummer: ");
        receiptString.append(certificateNumber);
        receiptString.append("\r\n");

        receiptString.append("Sig-Voriger-Beleg: ");
        receiptString.append(signaturePreviousValue);
        receiptString.append("\r\n");

        receiptString.append("Signatur: ");
        receiptString.append(signature);
        receiptString.append("\r\n");

        receiptString.append("\r\n");

        return receiptString.toString();
    }

    public void checkNumbers(){
        BigInteger umBig = new BigInteger(receiptSetNormal.replaceAll("\\.", "").replaceAll(",", ""));
    }
    public boolean checkDate(){
        return true;
    }
    public int howManyWrongCentValues(){
        return 0;
    }

    public int calcNumberValuesOfReceiptStrings(){
        int wrongReceipts=0;
        String receiptSet =receiptSetNormal.replaceAll("\\.", "");
        if (receiptSet.substring(receiptSet.lastIndexOf(",") + 1).length() > 2) {
            wrongReceipts++;
        }
        receiptSetNormalNumber = new BigInteger(receiptSet.replaceAll(",", "")).doubleValue();

        receiptSet =receiptSetReduced1.replaceAll("\\.", "");
        if (receiptSet.substring(receiptSet.lastIndexOf(",") + 1).length() > 2) {
            wrongReceipts++;
        }
        receiptSetReduced1Number = new BigInteger(receiptSet.replaceAll(",", "")).doubleValue();

        receiptSet =receiptSetReduced2.replaceAll("\\.", "");
        if (receiptSet.substring(receiptSet.lastIndexOf(",") + 1).length() > 2) {
            wrongReceipts++;
        }
        receiptSetReduced2Number = new BigInteger(receiptSet.replaceAll(",", "")).doubleValue();

        receiptSet =receiptSetNull.replaceAll("\\.", "");
        if (receiptSet.substring(receiptSet.lastIndexOf(",") + 1).length() > 2) {
            wrongReceipts++;
        }
        receiptSetNullNumber = new BigInteger(receiptSet.replaceAll(",", "")).doubleValue();

        receiptSet =receiptSetSpecial.replaceAll("\\.", "");
        if (receiptSet.substring(receiptSet.lastIndexOf(",") + 1).length() > 2) {
            wrongReceipts++;
        }
        receiptSetSpecialNumber = new BigInteger(receiptSet.replaceAll(",", "")).doubleValue();

        return wrongReceipts;
    }

    public double calculateRevenueShouldBe(double revenueOld,String cryptoFileLocation,boolean isFirstReceipt, boolean errorBlockerCauseSTOorTRA, int forcounter) throws IOException {
        // Abfrage ob STO oder TRA (Trainings beleg oder
        // Storno Beleg)
        // oder Umsatz wert
       receiptProperEncrypted =true;
        if (revenueEncrypted.equals("U1RP")) {
            revenueDecrypted="STO";
            //TODO turn to int ?
            revenueShouldBeNumber = receiptSetNormalNumber + receiptSetReduced1Number + receiptSetReduced2Number + receiptSetNullNumber + receiptSetSpecialNumber + revenueOld;
            revenueShouldBe=Double.toString(revenueShouldBeNumber/100);
            return revenueShouldBeNumber;
        } else if (revenueEncrypted.equals("VFJB")) {
            revenueDecrypted="TRA";
            revenueShouldBe=null;
            return revenueOld;
        } else {

            revenueDecryptedNumber = (double) code.CalcNewValue(registerId, receiptId, revenueEncrypted, cryptoFileLocation);
            revenueDecrypted=Double.toString(revenueDecryptedNumber);

            revenueShouldBeNumber = receiptSetNormalNumber + receiptSetReduced1Number + receiptSetReduced2Number + receiptSetNullNumber + receiptSetSpecialNumber + revenueOld;
            if (revenueDecryptedNumber == revenueShouldBeNumber) {
                revenueShouldBe=Double.toString(revenueShouldBeNumber/100);
                return revenueShouldBeNumber;
            } else {
                if ((forcounter == 0 && isFirstReceipt) || errorBlockerCauseSTOorTRA) {
                    revenueShouldBe=revenueDecrypted;
                    //TODO: DO outside !
                    //errorBlockerCauseSTOorTRA = false;
                } else {
                    receiptProperEncrypted =false;
                    revenueShouldBe="FEHLER";
                }
                return revenueDecryptedNumber;
            }

        }
    }

    public boolean isReceiptProperEncrypted(){
        return receiptProperEncrypted;
    }

    public String getRevenueDecrypted() {
        return revenueDecrypted;
    }

    public void setRevenueDecrypted(String revenueDecrypted) {
        this.revenueDecrypted = revenueDecrypted;
    }

    public String getRevenueShouldBe() {
        return revenueShouldBe;
    }

    public void setRevenueShouldBe(String revenueShouldBe) {
        this.revenueShouldBe = revenueShouldBe;
    }

    public String getSignatureNextValue() {
        return signatureNextValue;
    }

    public void setSignatureNextValue(String signatureNextValue) {
        this.signatureNextValue = signatureNextValue;
    }

    public String getZda() {
        return zda;
    }

    public void setZda(String zda) {
        this.zda = zda;
    }

    public String getRegisterId() {
        return registerId;
    }

    public void setRegisterId(String registerId) {
        this.registerId = registerId;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public String getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(String receiptDate) {
        this.receiptDate = receiptDate;
    }

    public String getReceiptSetNormal() {
        return receiptSetNormal;
    }

    public void setReceiptSetNormal(String receiptSetNormal) {
        this.receiptSetNormal = receiptSetNormal;
    }

    public String getReceiptSetReduced1() {
        return receiptSetReduced1;
    }

    public void setReceiptSetReduced1(String receiptSetReduced1) {
        this.receiptSetReduced1 = receiptSetReduced1;
    }

    public String getReceiptSetReduced2() {
        return receiptSetReduced2;
    }

    public void setReceiptSetReduced2(String receiptSetReduced2) {
        this.receiptSetReduced2 = receiptSetReduced2;
    }

    public String getReceiptSetNull() {
        return receiptSetNull;
    }

    public void setReceiptSetNull(String receiptSetNull) {
        this.receiptSetNull = receiptSetNull;
    }

    public String getReceiptSetSpecial() {
        return receiptSetSpecial;
    }

    public void setReceiptSetSpecial(String receiptSetSpecial) {
        this.receiptSetSpecial = receiptSetSpecial;
    }

    public String getRevenueEncrypted() {
        return revenueEncrypted;
    }

    public void setRevenueEncrypted(String revenueEncrypted) {
         this.revenueEncrypted = revenueEncrypted;
    }

    public String getCertificateNumber() {
        return certificateNumber;
    }

    public void setCertificateNumber(String certificateNumber) {
        this.certificateNumber = certificateNumber;
    }

    public String getSignaturePreviousValue() {
        return signaturePreviousValue;
    }

    public void setSignaturePreviousValue(String signaturePreviousValue) {
        this.signaturePreviousValue = signaturePreviousValue;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(int receiptNumber) {
        this.receiptNumber = receiptNumber;
    }
}
