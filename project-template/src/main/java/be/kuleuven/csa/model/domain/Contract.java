package be.kuleuven.csa.model.domain;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Contract implements CsaEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int contractID;
    @OneToOne
    @JoinColumn
    private Pakket pakketnaam;
    @OneToOne
    @JoinColumn
    private Klant klantID;
    @OneToOne
    @JoinColumn
    private Landbouwbedrijf ondernemingsNR;
    @Column
    private Date begindatum;

    public Contract(int contractID, Pakket pakketnaam, Klant klantID, Landbouwbedrijf ondernemingsNR, Date begindatum) {
        this.contractID = contractID;
        this.pakketnaam = pakketnaam;
        this.klantID = klantID;
        this.ondernemingsNR = ondernemingsNR;
        this.begindatum = begindatum;
    }
}
