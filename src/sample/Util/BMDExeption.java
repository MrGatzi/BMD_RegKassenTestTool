package sample.Util;

public class BMDExeption extends Exception {
    private String message;
    private String title;
    private Exeptionstyp typ;

    public BMDExeption(Throwable e) {
        super("Something went wrong. Please contact the Developer!", e);
        message = "Something went wrong. Please contact the Developer!";
        typ = Exeptionstyp.ERROR;
        title = "Unexpected Error";
    }

    public BMDExeption() {
        super("Something went wrong. Please contact the Developer!");
        message = "Something went wrong. Please contact the Developer!";
        typ = Exeptionstyp.ERROR;
        title = "Unexpected Error";
    }

    public BMDExeption(String message, String titel, Exeptionstyp typ) {
        super(message);
        this.title = titel;
        this.message = message;
        this.typ = typ;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String titel) {
        titel = titel;
    }

    public Exeptionstyp getTyp() {
        return typ;
    }

    public void setTyp(Exeptionstyp typ) {
        this.typ = typ;
    }
}
