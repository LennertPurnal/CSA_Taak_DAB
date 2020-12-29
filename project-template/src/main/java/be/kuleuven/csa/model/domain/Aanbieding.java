package be.kuleuven.csa.model.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Aanbieding implements CsaEntity, Serializable {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pakketnaam")
    private Pakket pakket;
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ondernemingsNR")
    private Landbouwbedrijf landbouwbedrijf;
    @Column
    private int prijs;


    public Aanbieding(Pakket pakket, Landbouwbedrijf landbouwbedrijf, int prijs) {
        this.pakket = pakket;
        this.landbouwbedrijf = landbouwbedrijf;
        this.prijs = prijs;
    }

    public Aanbieding() {
    }

    public Pakket getPakket() {
        return pakket;
    }

    public void setPakket(Pakket pakket) {
        this.pakket = pakket;
    }

    public Landbouwbedrijf getLandbouwbedrijf() {
        return landbouwbedrijf;
    }

    public void setLandbouwbedrijf(Landbouwbedrijf landbouwbedrijf) {
        this.landbouwbedrijf = landbouwbedrijf;
    }

    public int getPrijs() {
        return prijs;
    }

    public void setPrijs(int prijs) {
        this.prijs = prijs;
    }
}
