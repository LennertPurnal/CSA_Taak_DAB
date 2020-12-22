package be.kuleuven.csa.model.domain;

public class Pakket {
    private String pakketnaam;
    private int aantal_volwassenen;
    private int aantal_kinderen;
    private String beschrijving;

    public Pakket(String pakketnaam, int aantal_volwassenen, int aantal_kinderen, String beschrijving) {
        this.pakketnaam = pakketnaam;
        this.aantal_volwassenen = aantal_volwassenen;
        this.aantal_kinderen = aantal_kinderen;
        this.beschrijving = beschrijving;
    }
}
