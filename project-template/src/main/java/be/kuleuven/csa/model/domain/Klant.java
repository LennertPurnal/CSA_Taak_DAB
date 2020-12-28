package be.kuleuven.csa.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Klant implements CsaEntity{
    @Column
    private String naam;
    @Id
    @GeneratedValue
    private int klantID;
    @Column
    private String gemeente;
    @Column
    private int postcode;
    @Column
    private String straat;
    @Column
    private int huisnummer;
    @Column
    private String land;

    public Klant(String naam, String gemeente, int postcode, String straat, int huisnummer, String land) {
        this.naam = naam;
        this.gemeente = gemeente;
        this.postcode = postcode;
        this.straat = straat;
        this.huisnummer = huisnummer;
        this.land = land;
    }

    public Klant() {
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
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

    public String getStraat() {
        return straat;
    }

    public void setStraat(String straat) {
        this.straat = straat;
    }

    public int getHuisnummer() {
        return huisnummer;
    }

    public void setHuisnummer(int huisnummer) {
        this.huisnummer = huisnummer;
    }

    public String getLand() {
        return land;
    }

    public void setLand(String land) {
        this.land = land;
    }

    public int getKlantID() {
        return klantID;
    }
}
