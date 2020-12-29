package be.kuleuven.csa.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Pakket implements CsaEntity{
    @Id
    private String pakketnaam;
    @Column(name = "'aantal volwassenen'")
    private int aantal_volwassenen;
    @Column(name = "'aantal kinderen'")
    private int aantal_kinderen;
    @Column
    private String beschrijving;

    @OneToMany(mappedBy = "pakket")
    private List<Aanbieding> aanbiedingen = new ArrayList<>();

    @OneToMany(mappedBy = "pakket")
    private List<Contract> contracten = new ArrayList<>();

    public Pakket(String pakketnaam, int aantal_volwassenen, int aantal_kinderen, String beschrijving) {
        this.pakketnaam = pakketnaam;
        this.aantal_volwassenen = aantal_volwassenen;
        this.aantal_kinderen = aantal_kinderen;
        this.beschrijving = beschrijving;
    }

    public Pakket() {
    }

    public void voegAanbiedingToe(Aanbieding aanbieding){
        aanbiedingen.add(aanbieding);
    }

    public void voegContractToe(Contract contract){contracten.add(contract); }

    public String getPakketnaam() {
        return pakketnaam;
    }

    public void setPakketnaam(String pakketnaam) {
        this.pakketnaam = pakketnaam;
    }

    public int getAantal_volwassenen() {
        return aantal_volwassenen;
    }

    public void setAantal_volwassenen(int aantal_volwassenen) {
        this.aantal_volwassenen = aantal_volwassenen;
    }

    public int getAantal_kinderen() {
        return aantal_kinderen;
    }

    public void setAantal_kinderen(int aantal_kinderen) {
        this.aantal_kinderen = aantal_kinderen;
    }

    public String getBeschrijving() {
        return beschrijving;
    }

    public void setBeschrijving(String beschrijving) {
        this.beschrijving = beschrijving;
    }
}
