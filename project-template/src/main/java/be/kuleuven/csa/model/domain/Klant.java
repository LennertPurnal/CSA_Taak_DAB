package be.kuleuven.csa.model.domain;

public class Klant {
    private String naam;
    private int klantID;
    private String gemeente;
    private int postcode;
    private String straat;
    private int huisnummmer;
    private String land;

    public Klant(String naam, int klantID, String gemeente, int postcode, String straat, int huisnummmer) {
        this.naam = naam;
        this.klantID = klantID;
        this.gemeente = gemeente;
        this.postcode = postcode;
        this.straat = straat;
        this.huisnummmer = huisnummmer;
    }
}
