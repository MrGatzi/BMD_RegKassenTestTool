package sample.Util;

import java.io.File;
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

    static String startFolderProp = "startFolder";
    static String depFileProp = "depFiles";
    static String depKeyFilesProp = "depKeyFiles";
    static String qrFilesProp = "qrFiles";
    static String qrKeyFilesProp = "qrKeyFiles";
    static String advDepKeyFilesProp = "advDepKeyFiles";
    static String advDepFilesProp = "advDepFiles";
    static String junkFolderProp = "junkFolder";
    static String popUpProp = "popUp";
    static String askQuestionProp = "askQuestion";
    static String selectedRamInputProp = "selectedRamInput";
    static String ramInputProp = "ramInput";
    static String externalDepToolLocationProp = "externalDepToolLocation";
    static String externalQrToolLocationProp = "externalQrToolLocation";
    static String useDefaultDepToolProp = "useDefaultDepTool";


    private static String startFolder;
    private static String junkFolder;
    private static String selectedRamInput;
    private static String externalDepToolLocation;
    private static String externalQrToolLocation;
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

        startFolder = props.getProperty(startFolderProp);
        junkFolder = props.getProperty(junkFolderProp);
        externalDepToolLocation = props.getProperty(externalDepToolLocationProp);
        externalQrToolLocation = props.getProperty(externalQrToolLocationProp);
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

    public static boolean isUseDefaultDepTool() {
        return useDefaultDepTool;
    }

    public void setUseDefaultDepTool(boolean useDefaultDepTool) {
        this.useDefaultDepTool = useDefaultDepTool;
    }

    public static List<String> getAdvDepKeyFiles() {
        return advDepKeyFiles;
    }

    public void setAdvDepKeyFiles(List<String> advDepKeyFiles) {
        this.advDepKeyFiles = advDepKeyFiles;
    }

    public static List<String> getAdvDepFiles() {
        return advDepFiles;
    }

    public void setAdvDepFiles(List<String> advDepFiles) {
        this.advDepFiles = advDepFiles;
    }

    public static String getExternalDepToolLocation() {
        return externalDepToolLocation;
    }

    public void setExternalDepToolLocation(String externalDepToolLocation) {
        this.externalDepToolLocation = externalDepToolLocation;
    }

    public static String getExternalQrToolLocation() {
        return externalQrToolLocation;
    }

    public void setExternalQrToolLocation(String externalQrToolLocation) {
        this.externalDepToolLocation = externalDepToolLocation;
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
        props.put(externalDepToolLocationProp, externalDepToolLocation);
        props.put(externalQrToolLocationProp, externalQrToolLocation);
        props.put(useDefaultDepToolProp, Boolean.toString(useDefaultDepTool));
        props.put(startFolderProp, startFolder);
        props.put(junkFolderProp, junkFolder);
        props.put(selectedRamInputProp, selectedRamInput);
        props.put(popUpProp, Boolean.toString(popUp));
        props.put(askQuestionProp, Boolean.toString(askQuestion));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        File file = new File ("config.properties");
        if(!file.setWritable(true,false)){
            //TODO: Permission is missing ! throw error!
        }
        file.setReadable(true,false);
        file.setExecutable(true,false);
        props.store(new FileOutputStream(file), "Safed by dependencies -" + formatter.format(date));
    }

    public List<String> getRamInput() {
        return ramInput;
    }

    public String getSelectedRamInput() {
        return selectedRamInput;
    }

    public void setSelectedRamInput(String selectedRamInput) {
        this.selectedRamInput = selectedRamInput;
    }

    public boolean isAskQuestion() {
        return askQuestion;
    }

    public void setAskQuestion(boolean askQuestion) {
        this.askQuestion = askQuestion;
    }

    public boolean isPopUp() {
        return popUp;
    }

    public void setPopUp(boolean popUp) {
        this.popUp = popUp;
    }

    public String getJunkFolder() {
        return junkFolder;
    }

    public void setJunkFolder(String junkFolder) {
        this.junkFolder = junkFolder;
    }

    public String getStartFolder() {
        return startFolder;
    }

    public void setStartFolder(String startFolder) {
        this.startFolder = startFolder;
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


