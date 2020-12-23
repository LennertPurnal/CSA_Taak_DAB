package be.kuleuven.csa.model.domain;

import java.util.Map;


public class WekelijkseBestelling implements CsaEntity{
    private int bestelNR;
    private int klantID;
    private int contractID; //valt nog te bekijken (misschien beter aanbieding?)
    private Map<Product,Integer> producten;

    public WekelijkseBestelling(int bestelNR, int klantID, int contractID) {
        this.bestelNR = bestelNR;
        this.klantID = klantID;
        this.contractID = contractID;
    }
}
