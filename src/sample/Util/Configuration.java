package sample.Util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class Configuration {

    static String versionNumberProp = "versionNumber";
    static String startFolderProp = "startFolder";
    static String depFileProp = "depFiles";
    static String depKeyFilesProp = "depKeyFiles";
    static String qrFilesProp = "qrFiles";
    static String qrKeyFilesProp = "qrKeyFiles";
    static String advDepKeyFilesProp = "advDepKeyFiles";
    static String advDepFilesProp = "advDepFiles";
    static String junkFolderProp = "junkFolder";
    static String popUpProp = "popUp;";
    static String askQuestionProp = "askQuestion";
    static String selectedRamInputProp = "selectedRamInput";
    static String ramInputProp = "ramInput";
    static String externalDepToolLocationProp = "externalDepToolLocation";
    static String useDefaultDepToolProp = "useDefaultDepTool";

    private static String versionNumber;
    private static String startFolder;
    private static String junkFolder;
    private static String selectedRamInput;
    private static String externalDepToolLocation;
    private static boolean useDefaultDepTool;
    private static boolean popUp;
    private static boolean askQuestion;
    private static List<String> ramInput;
    private static List<String> depFiles;
    private static List<String> depKeyFiles;
    private static List<String> qrFiles;
    private static List<String> qrKeyFiles;
    private static List<String> advDepFiles;
    private static List<String> advDepKeyFiles;


    public Configuration() {

    }

    private Configuration(String versionNumber, String startFolder, List<String> DepFiles, List<String> DepKeyFiles, List<String> QrFiles, List<String> QrKeyFiles) {

    }

    public static Configuration create() throws IOException {

        Properties props = new Properties();
        props.load(new FileInputStream("config.properties"));

        versionNumber = props.getProperty(versionNumberProp);
        startFolder = props.getProperty(startFolderProp);
        junkFolder = props.getProperty(junkFolderProp);
        externalDepToolLocation = props.getProperty(externalDepToolLocationProp);
        selectedRamInput = props.getProperty(selectedRamInputProp);
        popUp = Boolean.parseBoolean(props.getProperty(popUpProp));
        useDefaultDepTool = Boolean.parseBoolean(props.getProperty(useDefaultDepToolProp));
        askQuestion = Boolean.parseBoolean(props.getProperty(askQuestionProp));
        depFiles = Arrays.asList(props.getProperty(depFileProp).split("\\s*,\\s*"));
        depKeyFiles = Arrays.asList(props.getProperty(depKeyFilesProp).split("\\s*,\\s*"));
        qrFiles = Arrays.asList(props.getProperty(qrFilesProp).split("\\s*,\\s*"));
        qrKeyFiles = Arrays.asList(props.getProperty(qrKeyFilesProp).split("\\s*,\\s*"));
        advDepFiles = Arrays.asList(props.getProperty(advDepFilesProp).split("\\s*,\\s*"));
        advDepKeyFiles = Arrays.asList(props.getProperty(advDepKeyFilesProp).split("\\s*,\\s*"));
        ramInput = Arrays.asList(props.getProperty(ramInputProp).split("\\s*,\\s*"));

        return new Configuration();
    }


    public void safe() throws IOException {
        Properties props = new Properties();
        props.put(depFileProp, String.join(",", depFiles));
        props.put(depKeyFilesProp, String.join(",", depKeyFiles));
        props.put(qrFilesProp, String.join(",", qrFiles));
        props.put(qrKeyFilesProp, String.join(",", qrKeyFiles));
        props.put(advDepFilesProp, String.join(",", advDepFiles));
        props.put(advDepKeyFilesProp, String.join(",", advDepKeyFiles));
        props.put(ramInputProp, String.join(",", ramInput));
        props.put(versionNumberProp, versionNumber);
        props.put(externalDepToolLocationProp,externalDepToolLocation);
        props.put(useDefaultDepToolProp,Boolean.toString(useDefaultDepTool));
        props.put(startFolderProp, startFolder);
        props.put(junkFolderProp, junkFolder);
        props.put(selectedRamInputProp, selectedRamInput);
        props.put(popUpProp, Boolean.toString(popUp));
        props.put(askQuestionProp, Boolean.toString(askQuestion));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        props.store(new FileOutputStream("config.properties"), "Safed by dependencies -" + formatter.format(date));
    }

    public static boolean isUseDefaultDepTool() {
        return useDefaultDepTool;
    }

    public static void setUseDefaultDepTool(boolean useDefaultDepTool) {
        Configuration.useDefaultDepTool = useDefaultDepTool;
    }

    public static String getExternalDepToolLocation() {
        return externalDepToolLocation;
    }

    public static void setExternalDepToolLocation(String externalDepToolLocation) {
        Configuration.externalDepToolLocation = externalDepToolLocation;
    }

    public List<String> getRamInput() {
        return ramInput;
    }

    public void setSelectedRamInput(String selectedRamInput) {
        Configuration.selectedRamInput = selectedRamInput;
    }

    public String getSelectedRamInput() {
        return selectedRamInput;
    }

    public boolean isAskQuestion() {
        return askQuestion;
    }

    public void setAskQuestion(boolean askQuestion) {
        Configuration.askQuestion = askQuestion;
    }

    public boolean isPopUp() {
        return popUp;
    }

    public void setPopUp(boolean popUp) {
        Configuration.popUp = popUp;
    }

    public String getJunkFolder() {
        return junkFolder;
    }

    public void setJunkFolder(String junkFolder) {
        Configuration.junkFolder = junkFolder;
    }

    public String getStartFolder() {
        return startFolder;
    }

    public void setStartFolder(String startFolder) {
        this.startFolder = startFolder;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public List<String> getDepFiles() {
        return depFiles;
    }

    public void setDepFiles(List<String> depFiles) {
        this.depFiles = depFiles;
    }

    public List<String> getDepKeyFiles() {
        return depKeyFiles;
    }

    public void setDepKeyFiles(List<String> depKeyFiles) {
        this.depKeyFiles = depKeyFiles;
    }

    public List<String> getQrFiles() {
        return qrFiles;
    }

    public void setQrFiles(List<String> qrFiles) {
        this.qrFiles = qrFiles;
    }

    public List<String> getQrKeyFiles() {
        return qrKeyFiles;
    }

    public void setQrKeyFiles(List<String> qrKeyFiles) {
        this.qrKeyFiles = qrKeyFiles;
    }
}


