module com.bmd_regkassentesttool {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.media;

    requires org.controlsfx.controls;
    requires org.apache.commons.codec;
    requires com.google.gson;
    requires org.apache.commons.lang3;
    requires java.desktop;
    requires guava.r05;
    requires org.apache.commons.io;
    requires org.bouncycastle.provider;
    requires org.fxmisc.richtext;
    requires org.fxmisc.flowless;

    opens com.bmd_regkassentesttool to javafx.fxml;
    exports com.bmd_regkassentesttool;
    exports com.bmd_regkassentesttool.Controller;
}