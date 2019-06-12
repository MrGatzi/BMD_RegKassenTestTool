package sample.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import sample.Util.Configuration;
import sample.Util.IOTools;

public class __ShowQrFileInConsole {
	String[] QR_Code_Titels = { "", "ZDA: ", "Kassen-ID:", "Belegnummer:", "Beleg-Datum-Uhrzeit:",
			"Betrag-Satz-Normal:", "Betrag-Satz-Ermaessigt-1:", "Betrag-Satz-Ermaessigt-2:", "Betrag-Satz-Null:",
			"Betrag-Satz-Besonders:", "Stand-Umsatz-Zaehler-AES256-ICM_Entschl�sselt:", "Zertifikat-Seriennummer:",
			"Sig-Voriger-Beleg:", "Signatur:", "", "", "" }; // Array mit die vor einem Wert bei der "ShowDEP" und "schowQR" methode angezeigt werden
	//coding
	__Coding code = new __Coding();
	IOTools read = new IOTools(new Configuration());
	

	public String show(String show_4, String show_5) {
		StringBuilder outputstring = new StringBuilder();
		try {
			String Input = read.readTxtFile(show_4);

			Input = Input.substring(1);
			Input = Input.substring(0, Input.length() - 1);
			String[] parts = Input.split("\"");
			int Flag = 0;
			while (Flag < parts.length) {
				if (parts[Flag].length() > 5) {
					String[] parts2 = parts[Flag].split("_");
					int Flag2 = 0;
					String KassenID = "";
					String BelegID = "";
					while (Flag2 < parts2.length) {
						if (Flag2 == 2) {
							KassenID = parts2[Flag2];
						}
						if (Flag2 == 3) {
							BelegID = parts2[Flag2];
						}
						if (Flag2 == 10) {
							outputstring.append(
									"Stand-Umsatz-Zaehler-AES256-ICM_Verschl�sselt: " + parts2[Flag2] + "   \r\n");
							// Abfrage ob STO oder TRA (Trainings beleg oder
							// Storno Beleg)
							// oder Umsatz wert
							if (parts2[Flag2].equals("U1RP")) {
								String d = "STO";
								outputstring.append(QR_Code_Titels[Flag2] + "  " + d + "   \r\n");
							} else if (parts2[Flag2].equals("VFJB")) {
								String d = "TRA";
								outputstring.append(QR_Code_Titels[Flag2] + "  " + d + "  \r\n");
							} else {
								long i1 = code.CalcNewValue(KassenID, BelegID, parts2[Flag2],show_5);
								double d = (double) i1;
								d = d / 100;
								outputstring.append(QR_Code_Titels[Flag2] + "  " + d + "�   \r\n");
							}
						} else {
							outputstring.append(QR_Code_Titels[Flag2] + " " + parts2[Flag2] + "\r\n");
							// calculate Sig-Vorig-btertag for the next value
							if (Flag2 == 12) {
								String retun = code.GenerateJWSSig(parts[Flag]);

								MessageDigest md1 = MessageDigest.getInstance("sha-256");
								md1.update(retun.getBytes());
								byte[] digest1 = md1.digest();

								// extract number of bytes (N, defined in
								// RKsuite)
								// from
								// hash value
								int bytesToExtract1 = 8;
								byte[] conDigest1 = new byte[bytesToExtract1];
								System.arraycopy(digest1, 0, conDigest1, 0, bytesToExtract1);

								// encode value as BASE64 String ==> chainValue
								byte[] Sig_Nae_Beleg = Base64.encodeBase64(conDigest1, false);
								String Sig_Nae_Beleg_String = new String(Sig_Nae_Beleg, "UTF-8");
								outputstring.append("Sig_N�chster_Beleg_Calculated: " + Sig_Nae_Beleg_String + "\r\n");
							}

						}
						Flag2++;
					}

					// QR_Code_Titels
				}
				Flag++;
			}
		} catch (IOException | NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outputstring.toString();
	}
	public String runQRTest(String DefaultStringQR, String DefaultStringCRYPTO, boolean futurBox, String outputFile,boolean DetailsBox){
		Runtime runtime = Runtime.getRuntime();
		Process process = null;
		StringBuilder outputstring = new StringBuilder();
		String proString="java -Xmx1500m -jar regkassen-verification-receipts-1.1.1.jar";
		if(futurBox==true){
			proString+=" -f";
		}
		if(DetailsBox==true){
			proString+=" -v";
		}
		proString +=" -i " + DefaultStringQR + " -c " + DefaultStringCRYPTO+" -o ";
		if(outputFile!=null){
			File file = new File(outputFile);
			if(file.isDirectory()==true){
				proString+=outputFile;
			}
			else{
				proString+="OutputFiles";
			}
		}
		
		try {
			process=runtime.exec(proString);
		} catch (IOException e2) {
			System.out.println("Error while calling regkassen-verification-receipts-1.1.1.jar on __ShowQrFileInConsole.java on Line 106");
			e2.printStackTrace();
		}
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;

		// Funktionsblock zum schreiben auf die JTextaera
		// da die Jtextarea eine Character begrenzung in der Weite hat
		// (~~~105 Chars) und es Zeilen gibt die mehr beanspruchen
		// muss zuerst gepr�ft werden ob die Zeile gr��er ist. Wenn Sie
		// gr��er ist wird sie soo oft geteilt auf die JTextarea
		// geschrieben
		// bis keine Chars mehr vorhanden sind.
		// nach jeder geschriebenen Zeile wird die JTextarea um eine
		// "row" erweiterd
		// Am schluss wird der Courser wieder ganz am Anfang gestellt

		try {
			while ((line = br.readLine()) != null) {

				if (line.length() > 105) {
					int lineCounter = line.length();
					int whileFlag = 0;
					while (lineCounter - 105 > 0) {
						outputstring.append(line.substring(whileFlag, whileFlag + 105) + "\r\n");
						whileFlag = whileFlag + 105;
						lineCounter = lineCounter - 105;
					}
					outputstring.append(line.substring(whileFlag, line.length())+ "\r\n");
				} else {
					outputstring.append(line + "\r\n");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outputstring.toString();
	}
}
