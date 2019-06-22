package sample.Util;

import javafx.scene.control.TextArea;
import sample.Util.enums.ActionTyp;

import java.io.File;
import java.io.IOException;

public interface Result {
     File getOuputLocation();
     void setOuputLocation(File file);
}
