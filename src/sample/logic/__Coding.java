package sample.logic;

import java.awt.Cursor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;


//Classe in der alle Codezeilen, Funktionen enthalten sind die zum Codieren oder Entcodieren ben�tigt werden. 
public class __Coding {

    public String GenerateJWSSig(String Payload) throws UnsupportedEncodingException {
        String search = "_";
        String jwsSignatureString = Payload.substring(Payload.lastIndexOf(search) + 1, Payload.length());
        Payload = Payload.substring(0, Payload.lastIndexOf(search));
        String jwsHeader = "eyJhbGciOiJFUzI1NiJ9"; // ES256 Header for JWS
        byte[] jwsPayload = Base64.encodeBase64(Payload.getBytes(), true); // get
        // payload
        String jwsPayloadString = new String(jwsPayload, "UTF-8");
        String[] partsString = jwsPayloadString.split("=");
        jwsPayloadString = partsString[0];
        partsString = jwsSignatureString.split("=");
        jwsSignatureString = partsString[0];
        jwsSignatureString = jwsSignatureString.replace('+', '-').replace('/', '_');
        String jwsCompactRep = jwsHeader + "." + jwsPayloadString + "." + jwsSignatureString;
        jwsCompactRep = jwsCompactRep.replace("\n", "").replace("\r", "");

        return jwsCompactRep;
    }

    // Funktion die es einem base64URL decodiern l�sst.
    public String base64UrlDecode(String input) {
        String result = null;
        Base64 decoder = new Base64(true);
        byte[] decodedBytes = decoder.decode(input);
        result = new String(decodedBytes);
        return result;
    }

    // Funktion die es einem base64URL encodiern l�sst.
    public String base64UrlEncode(String input) {
        String result = null;
        Base64 encoder = new Base64(true);
        byte[] encoded = encoder.encode(input.getBytes());
        result = new String(encoded);
        return result;
    }

    public static byte[] base64Decode(String base64Data, boolean isUrlSafe) {
        Base64 decoder = new Base64(isUrlSafe);
        return decoder.decode(base64Data);
    }

    // Funktion die den Wert zur�ckgibt wenn ein verschl�sselter umsatzz�hler
    // eingegben wird.
    // Input sind KassenId / Beleg ID (wie im QR File auszulesen) und der
    // verschl�sselte Umsatz
    // es wird zuerst der AES Schl�ssel aus dem aktuellen Crypto File ausgelesen
    // (nach dem 3 ") (w�re auch mit Json gegangen)
    // der AES key wird Base64codiert und in die Entschl�ssunlungs Funktion
    // mit�bergeben
    public long CalcNewValue(String KassenID, String BelegID, String Umsatz, String FilePath) throws IOException {
        long NewValue1 = 99; // --> Kennzeichnet Fehler !
        File file = new File(FilePath);
        if (file.exists()) {
            FileInputStream inputStream = new FileInputStream(FilePath);
            try {
                String everything = "";
                try {
                    everything = IOUtils.toString(inputStream);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    System.out.println("Keinen Key im Crypto File !");
                }
                String[] parts = everything.split("\"");
                String key = parts[3];// 128 bit
                byte[] a = base64Decode(key, true);
                if (a.length < 32) {
                    a = base64Decode("WQRtiiya3hYh/Uz44Bv3x8ETl1nrH6nCdErn69g5/lU=", true);
                }
                SecretKey aesKey = new SecretKeySpec(a, "AES");// AES Key in
                // Byte Array
                // einf�gen !
                try {
                    NewValue1 = decryptTurnOverCounter(Umsatz, "sha-256", KassenID, BelegID, aesKey);
                } catch (Exception e) {

                    e.printStackTrace();
                }
            } finally {
                inputStream.close();
            }
        } else {
            System.out.println("#####     Crypto-File not Found!  No Decryption !   #####");
        }
        return NewValue1;
    }

    // Funktion zum Entschl�sseln des Umsatzz�hlers
    // Aus dem Github verzeichniss : https://github.com/a-sit-plus/at-registrierkassen-mustercode/blob/master/regkassen-core/src/main/java/at/asitplus/regkassen/core/base/util/CryptoUtil.java
    // den TurnOverCounter checken ob er TRO oder STO ist und wenn dann nicht in die If rein !
    public static long decryptTurnOverCounter(String encryptedTurnOverCounterBase64, String hashAlgorithm, String cashBoxIDUTF8String, String receiptIdentifierUTF8String, SecretKey aesKey) throws Exception {
        // calc IV value (cashbox if + Receipt identifer, both as UTF-8 Strings)
        String IVUTF8StringRepresentation = cashBoxIDUTF8String + receiptIdentifierUTF8String;

        // calc hash
        MessageDigest messageDigest = MessageDigest.getInstance(hashAlgorithm);
        byte[] hashValue = messageDigest.digest(IVUTF8StringRepresentation.getBytes());
        byte[] concatenatedHashValue = new byte[16];
        System.arraycopy(hashValue, 0, concatenatedHashValue, 0, 16);

        // extract bytes 0-15 from hash value
        ByteBuffer byteBufferIV = ByteBuffer.allocate(16);
        byteBufferIV.put(concatenatedHashValue);

        // IV for AES algorithm
        byte[] IV = byteBufferIV.array();

        // prepare AES cipher with CTR/ICM mode, NoPadding is essential for the
        // decryption process. Padding could not be reconstructed due
        // to storing only 8 bytes of the cipher text (not the full 16 bytes)
        // (or 5 bytes if the minimum turnover length is used)
        IvParameterSpec ivSpec = new IvParameterSpec(IV);

        // start decryption process
        ByteBuffer encryptedTurnOverValueComplete = ByteBuffer.allocate(16);

        // decode turnover base64 value
        byte[] encryptedTurnOverValue = base64Decode(encryptedTurnOverCounterBase64, false);

        // extract length (required to extract the correct number of bytes from
        // decrypted value
        int lengthOfEncryptedTurnOverValue = encryptedTurnOverValue.length;

        // prepare for decryption (require 128 bit blocks...)
        encryptedTurnOverValueComplete.put(encryptedTurnOverValue);
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        // decryption setup, AES ciper in CTR mode, NO PADDING!)
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, aesKey, ivSpec);

        // decrypt value, now we have a 128 bit block, with trailing junk bytes
        byte[] testPlainTurnOverValueComplete = cipher.doFinal(encryptedTurnOverValue);

        // remove junk bytes by extracting known length of plain text
        byte[] testPlainTurnOverValue = new byte[lengthOfEncryptedTurnOverValue];
        System.arraycopy(testPlainTurnOverValueComplete, 0, testPlainTurnOverValue, 0, lengthOfEncryptedTurnOverValue);
        //return
        return new BigInteger(testPlainTurnOverValue).longValue();

        // Alte BErechnung ! nicht l�nger relevant !
        // create java LONG out of ByteArray (avoid Error when ByteArray is less then 4)
			/*ByteBuffer plainTurnOverValueByteBuffer = ByteBuffer.wrap(testPlainTurnOverValue);
			if (plainTurnOverValueByteBuffer.remaining() > 4) {		
				return plainTurnOverValueByteBuffer.getLong();
			}else{
				long i=0;
				byte[] test = base64Decode(encryptedTurnOverCounterBase64,false);
				if(test[0]==83){
					i=98989898;
				}
				if(test[0]==84){
					i=97979797;
				}
				return i;
			}*/
    }
}
