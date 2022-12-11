package com.bmd_regkassentesttool.Util.Ui;

import java.io.File;

public class CostumComboBoxItem {
    private File file;

    public CostumComboBoxItem(String filename) {
        file = new File(filename);
    }

    public String getPathTo() {
        return this.file.getParent();
    }

    public String toString() {
        return this.file.getName();
    }

    public File getFile() {
        return this.file;
    }

    public String getPath(){
        return this.file.getAbsolutePath();
    }
}
