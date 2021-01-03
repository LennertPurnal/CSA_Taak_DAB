package be.kuleuven.csa.model.domain;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Entity
public class Contract implements CsaEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int contractID;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pakketnaam")
    private Pakket pakket;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "klantID")
    private Klant klant;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ondernemingsNR")
    private Landbouwbedrijf landbouwbedrijf;
    @Column
    private String begindatum;
    @Transient
    private String vervaldatum;


    public Contract(Pakket pakket, Klant klant, Landbouwbedrijf landbouwbedrijf, String begindatum) {
        this.pakket = pakket;
        this.klant = klant;
        this.landbouwbedrijf = landbouwbedrijf;
        this.begindatum = begindatum;

        calculateVervalDatum();
    }

    public void calculateVervalDatum(){
        Date beginDatum = null;
        try {
            beginDatum = new SimpleDateFormat("dd/MM/yyyy").parse(begindatum);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(beginDatum);
        c.add(Calendar.YEAR, 1);
        Date vervalDatum = c.getTime();
        this.vervaldatum = new SimpleDateFormat("dd/MM/yyyy").format(vervalDatum);
    }

    public void setContractID(int contractID) { this.contractID = contractID; }

    public int getContractID() {
        return contractID;
    }

    public Pakket getPakket() {
        return pakket;
    }

    public void setPakket(Pakket pakket) {
        this.pakket = pakket;
    }

    public Klant getKlant() {
        return klant;
    }

    public void setKlant(Klant klant) {
        this.klant = klant;
    }

    public Landbouwbedrijf getLandbouwbedrijf() {
        return landbouwbedrijf;
    }

    public void setLandbouwbedrijf(Landbouwbedrijf landbouwbedrijf) {
        this.landbouwbedrijf = landbouwbedrijf;
    }

    public String getBegindatum() {
        return begindatum;
    }

    public void setBegindatum(String begindatum) {
        this.begindatum = begindatum;
    }

    public String getVervaldatum() {
        return vervaldatum;
    }

    public void setVervaldatum(String vervaldatum) {
        this.vervaldatum = vervaldatum;
    }

    public Contract() {
    }
}
