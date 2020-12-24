package be.kuleuven.csa.model.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Landbouwbedrijf implements CsaEntity{
    @Id
    private int ondernemingsNR;
    @Column
    private String naam;
    @Column
    private String land;
    @Column
    private String gemeente;
    @Column
    private int postcode;

    @OneToMany(mappedBy = "landbouwbedrijf")
    private List<Aanbieding> aanbiedingen = new ArrayList<>();

    public Landbouwbedrijf() {} //default constructor

    public Landbouwbedrijf(int ondernemingsNR, String naam, String gemeente, int postcode) {
        this.ondernemingsNR = ondernemingsNR;
        this.naam = naam;
        this.gemeente = gemeente;
        this.postcode = postcode;
    }

    public void voegAanbiedingToe(Aanbieding aanbieding){
        aanbiedingen.add(aanbieding);
    }

    public int getOndernemingsNR() {
        return ondernemingsNR;
    }

    public void setOndernemingsNR(int ondernemingsNR) {
        this.ondernemingsNR = ondernemingsNR;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public String getLand() {
        return land;
    }

    public void setLand(String land) {
        this.land = land;
    }

    public String getGemeente() {
        return gemeente;
    }

    public void setGemeente(String gemeente) {
        this.gemeente = gemeente;
    }

    public int getPostcode() {
        return postcode;
    }

    public void setPostcode(int postcode) {
        this.postcode = postcode;
    }
}
