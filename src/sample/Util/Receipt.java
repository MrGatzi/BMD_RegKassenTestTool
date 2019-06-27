package sample.Util;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Receipt {
    CryptoTools code = new CryptoTools();
    //receipt values
    private String wholeReceipt;
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
    private String signaturePreviousValueCalculated;
    private String signatureNextValueCalculated;
    private String signature;
    // doubles for calclulations
    private double revenueShouldBeNumber;
    private double revenueDecryptedNumber;
    private double receiptSetNormalNumber;
    private double receiptSetReduced1Number;
    private double receiptSetReduced2Number;
    private double receiptSetNullNumber;
    private double receiptSetSpecialNumber;
    //booleans and flags for tests;
    private boolean receiptProperChained;
    private boolean revenueProperEncrypted;
    private int wrongReceiptSetValues;
    private boolean errorBlockerUsed;

    public Receipt() {
        wholeReceipt = null;
        this.receiptNumber = receiptNumber;
        zda = null;
        registerId = null;
        receiptId = null;
        receiptDate = null;
        receiptSetNormal = null;
        receiptSetReduced1 = null;
        receiptSetReduced2 = null;
        receiptSetNull = null;
        receiptSetSpecial = null;
        revenueEncrypted = null;
        revenueDecrypted = null;
        revenueShouldBe = null;
        certificateNumber = null;
        signaturePreviousValue = null;
        signatureNextValueCalculated = null;
        signaturePreviousValueCalculated = null;
        signature = null;
        revenueProperEncrypted = false;
        errorBlockerUsed=false;
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
        receiptString.append(revenueEncrypted);
        receiptString.append("\r\n");

        receiptString.append("Stand-Umsatz-Zaehler-AES256-ICM_Entschlüsselt: ");
        receiptString.append(revenueDecrypted);
        receiptString.append("\r\n");

        if (revenueShouldBe != null) {
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

        receiptString.append("Sig_Voriger_Beleg_Calculated: ");
        receiptString.append(signaturePreviousValueCalculated);
        receiptString.append("\r\n");

        receiptString.append("Sig_Nächster_Beleg_Calculated: ");
        receiptString.append(signatureNextValueCalculated);
        receiptString.append("\r\n");

        receiptString.append("Signatur: ");
        receiptString.append(signature);
        receiptString.append("\r\n");

        return receiptString.toString();
    }

    public boolean isRevenueProperEncrypted() {
        return revenueProperEncrypted;
    }

    public boolean isProperChained(){
        return receiptProperChained;
    }
    //calculation
    public int calculateNumberValuesOfReceiptStrings() {
        wrongReceiptSetValues = 0;
        String receiptSet = receiptSetNormal.replaceAll("\\.", "");
        if (receiptSet.substring(receiptSet.lastIndexOf(",") + 1).length() > 2) {
            wrongReceiptSetValues++;
        }
        receiptSetNormalNumber = new BigInteger(receiptSet.replaceAll(",", "")).doubleValue();

        receiptSet = receiptSetReduced1.replaceAll("\\.", "");
        if (receiptSet.substring(receiptSet.lastIndexOf(",") + 1).length() > 2) {
            wrongReceiptSetValues++;
        }
        receiptSetReduced1Number = new BigInteger(receiptSet.replaceAll(",", "")).doubleValue();

        receiptSet = receiptSetReduced2.replaceAll("\\.", "");
        if (receiptSet.substring(receiptSet.lastIndexOf(",") + 1).length() > 2) {
            wrongReceiptSetValues++;
        }
        receiptSetReduced2Number = new BigInteger(receiptSet.replaceAll(",", "")).doubleValue();

        receiptSet = receiptSetNull.replaceAll("\\.", "");
        if (receiptSet.substring(receiptSet.lastIndexOf(",") + 1).length() > 2) {
            wrongReceiptSetValues++;
        }
        receiptSetNullNumber = new BigInteger(receiptSet.replaceAll(",", "")).doubleValue();

        receiptSet = receiptSetSpecial.replaceAll("\\.", "");
        if (receiptSet.substring(receiptSet.lastIndexOf(",") + 1).length() > 2) {
            wrongReceiptSetValues++;
        }
        receiptSetSpecialNumber = new BigInteger(receiptSet.replaceAll(",", "")).doubleValue();

        return wrongReceiptSetValues;
    }

    public double calculateRevenueShouldBe(double revenueOld, String cryptoFileLocation, boolean isFirstReceiptNotIncluded, boolean errorBlockerCauseSTOorTRA) throws IOException {
        // Abfrage ob STO oder TRA (Trainings beleg oder Storno Beleg) oder Umsatz wert
        revenueProperEncrypted = true;
        if (revenueEncrypted.equals("U1RP")) {
            revenueDecrypted = "STO";
            revenueShouldBeNumber = receiptSetNormalNumber + receiptSetReduced1Number + receiptSetReduced2Number + receiptSetNullNumber + receiptSetSpecialNumber + revenueOld;
            revenueShouldBe = Double.toString(revenueShouldBeNumber / 100);
            return revenueShouldBeNumber;
        } else if (revenueEncrypted.equals("VFJB")) {
            revenueDecrypted = "TRA";
            revenueShouldBe = null;
            return revenueOld;
        } else {
            revenueDecryptedNumber = (double) code.CalcNewValue(registerId, receiptId, revenueEncrypted, cryptoFileLocation);
            revenueDecrypted = Double.toString(revenueDecryptedNumber / 100);

            revenueShouldBeNumber = receiptSetNormalNumber + receiptSetReduced1Number + receiptSetReduced2Number + receiptSetNullNumber + receiptSetSpecialNumber + revenueOld;
            if (revenueDecryptedNumber == revenueShouldBeNumber) {
                revenueShouldBe = Double.toString(revenueShouldBeNumber / 100);
                return revenueShouldBeNumber;
            } else {
                if ((receiptNumber == 0 && isFirstReceiptNotIncluded) || errorBlockerCauseSTOorTRA) {
                    revenueShouldBe = revenueDecrypted;
                    errorBlockerUsed =true;
                } else {
                    revenueProperEncrypted = false;
                    revenueShouldBe = "FEHLER";
                }
                return revenueDecryptedNumber;
            }

        }
    }

    public void calculatePreviousAndNextSignitarues(String inputForPreviousSignature) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        this.signaturePreviousValueCalculated = calculateSignature(inputForPreviousSignature);
        this.signatureNextValueCalculated = calculateSignature(wholeReceipt);
        if(signaturePreviousValueCalculated.equals(signaturePreviousValue)){
            receiptProperChained=true;
        }else{
            receiptProperChained=false;
            signaturePreviousValueCalculated=signaturePreviousValueCalculated+" -FEHLER";
        }
    }

    public String calculateSignature(String input) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest md1 = MessageDigest.getInstance("sha-256");

        //calculate hash value
        md1.update(input.getBytes());
        byte[] digest1 = md1.digest();

        // extract number of bytes from hash value
        int bytesToExtract1 = 8;
        byte[] conDigest1 = new byte[bytesToExtract1];
        System.arraycopy(digest1, 0, conDigest1, 0, bytesToExtract1);

        // encode value as BASE64 String ==> chainValue
        byte[] SignatureArray = Base64.encodeBase64(conDigest1, false);
        String SignatureString = new String(SignatureArray, "UTF-8");
        return SignatureString;
    }


    public boolean wasErrorBlockerUsed(){
        return errorBlockerUsed;
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

    public String getSignatureNextValueCalculated() {
        return signatureNextValueCalculated;
    }

    public void setSignatureNextValueCalculated(String signatureNextValueCalculated) {
        this.signatureNextValueCalculated = signatureNextValueCalculated;
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

    public String getSignaturePreviousValueCalculated() {
        return signaturePreviousValueCalculated;
    }

    public void setSignaturePreviousValueCalculated(String signaturePreviousValueCalculated) {
        this.signaturePreviousValueCalculated = signaturePreviousValueCalculated;
    }

    public String getWholeReceipt() {
        return wholeReceipt;
    }

    public void setWholeReceipt(String wholeReceipt) {
        this.wholeReceipt = wholeReceipt;
    }
}
