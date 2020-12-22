package be.kuleuven.csa.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Aanbieding {
    @Column
    private int ondernemingsNR;
    @Id
    private String pakketnaam;
    @Column
    private int prijs;


    public Aanbieding(int ondernemingsNR, String pakketnaam, int prijs) {
        this.ondernemingsNR = ondernemingsNR;
        this.pakketnaam = pakketnaam;
        this.prijs = prijs;
    }
}
