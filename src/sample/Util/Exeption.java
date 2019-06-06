package sample.Util;

public class Exeption {
    private String message;
    private String title;
    private Exeptionstyp typ;

    public Exeption(String message, String titel, Exeptionstyp typ) {
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
