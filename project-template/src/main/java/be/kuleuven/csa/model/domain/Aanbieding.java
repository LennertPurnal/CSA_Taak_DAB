package be.kuleuven.csa.model.domain;

public class Aanbieding {
    private int ondernemingsNR;
    private String pakketnaam;
    private int prijs;

    public Aanbieding(int ondernemingsNR, String pakketnaam, int prijs) {
        this.ondernemingsNR = ondernemingsNR;
        this.pakketnaam = pakketnaam;
        this.prijs = prijs;
    }
}
