package com.bmd_regkassentesttool.Util.Factories;

import com.bmd_regkassentesttool.Util.Configuration;
import com.bmd_regkassentesttool.Util.Enums.ResultTyp;

import java.io.File;
import java.io.IOException;


public class TmpFactory {

    Configuration config;

    public TmpFactory(Configuration config) {
        this.config = config;
    }

    public File getNewTmpFile(ResultTyp typ) throws IOException {
        File junkFolder = new File(config.getJunkFolder());

        if (!junkFolder.exists()) {
            junkFolder.mkdir();
        }
        String name = "";
        switch (typ) {
            case RUNDEPTEST:
                name = "TestResult";
                break;
            case SHOWDEPFILE:
                name = "DepFile";
                break;
            case FILTERDEPTEST:
                name = "FilterResult";
                break;
            case ADVDEPTEST:
                name = "AdvDepTest";
                break;
        }
        File tempFile = File.createTempFile("_" + name, ".tmp.txt", junkFolder);
        tempFile.deleteOnExit();
        return tempFile;
    }

    public File getNewJsonTmpFile(ResultTyp typ) throws IOException {
        File junkFolder = new File(config.getJunkFolder());

        if (!junkFolder.exists()) {
            junkFolder.mkdir();
        }
        String name = "";
        switch (typ) {
            case RUNDEPTEST:
                name = "TestResult";
                break;
            case SHOWDEPFILE:
                name = "DepFile";
                break;
            case FILTERDEPTEST:
                name = "FilterResult";
                break;
            case ADVDEPTEST:
                name = "AdvDepTest";
                break;
        }
        File tempFile = File.createTempFile("_" + name, ".tmp.json", junkFolder);
        tempFile.deleteOnExit();
        return tempFile;
    }

    public File getNewJsonTmpFile(String name, int number) throws IOException {
        File junkFolder = new File(config.getJunkFolder());

        if (!junkFolder.exists()) {
            junkFolder.mkdir();
        }

        File tempFile = File.createTempFile("_" + name + number + "_", ".tmp.json", junkFolder);
        tempFile.deleteOnExit();
        return tempFile;
    }

    public File getNewTxtTmpFile(String name, int number) throws IOException {
        File junkFolder = new File(config.getJunkFolder());

        if (!junkFolder.exists()) {
            junkFolder.mkdir();
        }

        File tempFile = File.createTempFile("_" + name + number + "_", ".tmp.txt", junkFolder);
        tempFile.deleteOnExit();
        return tempFile;
    }

    public File getNewTxtTmpFile(String name) throws IOException {
        File junkFolder = new File(config.getJunkFolder());

        if (!junkFolder.exists()) {
            junkFolder.mkdir();
        }

        File tempFile = File.createTempFile("_" + name + "_", ".tmp.json", junkFolder);
        tempFile.deleteOnExit();
        return tempFile;
    }

}
