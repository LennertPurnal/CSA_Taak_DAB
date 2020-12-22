package be.kuleuven.csa.model.domain;

public class Landbouwbedrijf {
    private int ondernemingsNR;
    private String naam;
    private String land;
    private String gemeente;
    private int postcode;

    public Landbouwbedrijf(int ondernemingsNR, String naam, String gemeente, int postcode) {
        this.ondernemingsNR = ondernemingsNR;
        this.naam = naam;
        this.gemeente = gemeente;
        this.postcode = postcode;
    }
}
