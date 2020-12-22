package be.kuleuven.csa.model.domain;

import java.util.Date;

public class Contract {
    private int contractID;
    private String pakketnaam;
    private int klantID;
    private int ondernemingsNR;
    private Date begindatum;

    public Contract(int contractID, String pakketnaam, int klantID, int ondernemingsNR, Date begindatum) {
        this.contractID = contractID;
        this.pakketnaam = pakketnaam;
        this.klantID = klantID;
        this.ondernemingsNR = ondernemingsNR;
        this.begindatum = begindatum;
    }
}
