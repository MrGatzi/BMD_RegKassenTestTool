package sample.Util;

import javafx.scene.control.TextArea;

import java.io.File;
import java.io.IOException;

public interface Result {
     File getOuputLocation();
     void setOuputLocation(File file);
}
