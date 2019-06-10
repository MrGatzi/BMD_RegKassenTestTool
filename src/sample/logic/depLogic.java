package sample.logic;

import java.io.*;

import
import sample.Util.IOTools;

public class depLogic {

    //Run DEP-Test
    public String runDepTest(String DefaultStringDEP, String DefaultStringCRYPTO, boolean futurBox, String outputFile, boolean DetailsBox) {
        StringBuilder outputstring = new StringBuilder();

        Process process = null;
        IOTools iOTools = new IOTools();
        String processString = iOTools.createDepProcessString(DefaultStringDEP, DefaultStringCRYPTO, outputFile, futurBox, DetailsBox);

        try {
            process = Runtime.getRuntime().exec(processString);
        } catch (IOException e) {
            System.out.println("Error while calling regkassen-verification-depformat-1.1.1.jar on __ShowDEPFileInConsole.java on Line 290");
            e.printStackTrace();
        }
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        // Funktionsblock zum schreiben auf die JTextaera
        // da die Jtextarea eine Character begrenzung in der Weite hat
        // (~~~105 Chars) und es Zeilen gibt die mehr beanspruchen
        // muss zuerst geprüft werden ob die Zeile größer ist. Wenn Sie
        // größer ist wird sie soo oft geteilt auf die JTextarea
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
                    outputstring.append(line.substring(whileFlag, line.length()) + "\r\n");
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
