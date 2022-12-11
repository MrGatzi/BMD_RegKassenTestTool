package com.bmd_regkassentesttool.Util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Version {
    static String versionNumberProp = "versionNumber";
    private static String versionNumber;

    public Version() throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream("version.properties"));

        versionNumber = props.getProperty(versionNumberProp);
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }
}
