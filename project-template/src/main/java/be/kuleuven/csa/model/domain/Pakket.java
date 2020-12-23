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
    @Column
    private int aantal_volwassenen;
    @Column
    private int aantal_kinderen;
    @Column
    private String beschrijving;

    @OneToMany(mappedBy = "pakket")
    private List<Aanbieding> aanbiedingen = new ArrayList<>();

    public Pakket(String pakketnaam, int aantal_volwassenen, int aantal_kinderen, String beschrijving) {
        this.pakketnaam = pakketnaam;
        this.aantal_volwassenen = aantal_volwassenen;
        this.aantal_kinderen = aantal_kinderen;
        this.beschrijving = beschrijving;
    }

    public void voegAanbiedingToe(Aanbieding aanbieding){
        aanbiedingen.add(aanbieding);
    }
}
