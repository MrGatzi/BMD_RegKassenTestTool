package sample.Util;

import sample.logic.__Coding;

import java.io.IOException;
import java.math.BigInteger;

public class Receipt {
    String[] QR_Code_Titels = {"", "ZDA: ", "Kassen-ID:", "Belegnummer:", "Beleg-Datum-Uhrzeit:",
            "Betrag-Satz-Normal:", "Betrag-Satz-Ermaessigt-1:", "Betrag-Satz-Ermaessigt-2:", "Betrag-Satz-Null:",
            "Betrag-Satz-Besonders:", "Stand-Umsatz-Zaehler-AES256-ICM_Entschlüsselt:", "Zertifikat-Seriennummer:",
            "Sig-Voriger-Beleg:", "Signatur:", "", "", ""}; // Array mit die vor einem Wert bei der "ShowDEP" und "schowQR" methode angezeigt werden

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

    __Coding code = new __Coding();

    public Receipt(){
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
    }

    public String toString() {
        return "";
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

    public void calculateRevenueShouldBe(double revenueOld,String cryptoFileLocation,boolean isFirstReceipt) throws IOException {
        // Abfrage ob STO oder TRA (Trainings beleg oder
        // Storno Beleg)
        // oder Umsatz wert
        boolean properEncryption=false;
        if (revenueEncrypted.equals("U1RP")) {
            revenueDecrypted="STO";
            //TODO turn to in ?
            revenueShouldBe = receiptSetNormal + receiptSetReduced1 + receiptSetReduced2 + receiptSetNull+ receiptSetSpecial + revenueOld;
            revenueOld = revenueShouldBeNumber;
            revenueShouldBeNumber = revenueShouldBeNumber/100;
            properEncryption=true;
        } else if (revenueEncrypted.equals("VFJB")) {
            revenueDecrypted="TRA";
            properEncryption=true;
        } else {

            long i1 = code.CalcNewValue(registerId, receiptId, revenueDecrypted, cryptoFileLocation);
            double d = (double) i1;
            double dflag = d / 100;
            revenueDecryptedNumber=dflag;
            revenueShouldBe = receiptSetNormal + receiptSetReduced1 + receiptSetReduced2 + receiptSetNull+ receiptSetSpecial + revenueOld;
            revenueOld = revenueShouldBeNumber;
            if (d == revenueShouldBeNumber) {
                properEncryption=true;
                revenueShouldBe=""+(revenueShouldBeNumber/100);

            } else {
                if ((forcounter == 0 && isFirstReceipt) || errorBlockerCauseSTOorTRA) {
                    properEncryption=true;
                    revenueOld = d;
                    revenueShouldBe=""+dflag;
                            //TODO: DO outside !
                    //errorBlockerCauseSTOorTRA = false;
                } else {
                    properEncryption=false;
                    revenueOld = d;
                    revenueShouldBe="FEHLER!";
                }
            }

        }
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
}
