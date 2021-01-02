package be.kuleuven.csa.model.domain;

public class Tip {

    private String tipBeschrijving;
    private String uitleg;

    public Tip(String tipBeschrijving, String uitleg) {
        this.tipBeschrijving = tipBeschrijving;
        this.uitleg = uitleg;
    }

    public String getTipBeschrijving() {
        return tipBeschrijving;
    }

    public void setTipBeschrijving(String tipBeschrijving) {
        this.tipBeschrijving = tipBeschrijving;
    }

    public String getUitleg() {
        return uitleg;
    }

    public void setUitleg(String uitleg) {
        this.uitleg = uitleg;
    }
}
