package sample.Util;

public class Receipt {
    String[] QR_Code_Titels = {"", "ZDA: ", "Kassen-ID:", "Belegnummer:", "Beleg-Datum-Uhrzeit:",
            "Betrag-Satz-Normal:", "Betrag-Satz-Ermaessigt-1:", "Betrag-Satz-Ermaessigt-2:", "Betrag-Satz-Null:",
            "Betrag-Satz-Besonders:", "Stand-Umsatz-Zaehler-AES256-ICM_Entschlüsselt:", "Zertifikat-Seriennummer:",
            "Sig-Voriger-Beleg:", "Signatur:", "", "", ""}; // Array mit die vor einem Wert bei der "ShowDEP" und "schowQR" methode angezeigt werden

    private String zda;
    private String registerId;
    private String receiptId;
    private String receiptDate;
    private double receiptSetNormal;
    private double receiptSetReduced1;
    private double receiptSetReduced2;
    private double receiptSetNull;
    private double receiptSetSpecial;
    private String revenueDecrypted;
    private String revenueEncrypted;
    private String revenueShouldBe;
    private String certificateNumber;
    private String signaturePreviousValue;
    private String signatureNextValue;
    private String signature;


    public Receipt(){
        zda=null;
        registerId=null;
        receiptId =null;
        receiptDate=null;
        receiptSetNormal=0;
        receiptSetReduced1=0;
        receiptSetReduced2=0;
        receiptSetNull=0;
        receiptSetSpecial=0;
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

    public double getReceiptSetNormal() {
        return receiptSetNormal;
    }

    public void setReceiptSetNormal(double receiptSetNormal) {
        this.receiptSetNormal = receiptSetNormal;
    }

    public double getReceiptSetReduced1() {
        return receiptSetReduced1;
    }

    public void setReceiptSetReduced1(double receiptSetReduced1) {
        this.receiptSetReduced1 = receiptSetReduced1;
    }

    public double getReceiptSetReduced2() {
        return receiptSetReduced2;
    }

    public void setReceiptSetReduced2(double receiptSetReduced2) {
        this.receiptSetReduced2 = receiptSetReduced2;
    }

    public double getReceiptSetNull() {
        return receiptSetNull;
    }

    public void setReceiptSetNull(double receiptSetNull) {
        this.receiptSetNull = receiptSetNull;
    }

    public double getReceiptSetSpecial() {
        return receiptSetSpecial;
    }

    public void setReceiptSetSpecial(double receiptSetSpecial) {
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
