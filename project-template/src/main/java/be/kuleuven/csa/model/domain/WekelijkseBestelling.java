package be.kuleuven.csa.model.domain;


public class WekelijkseBestelling implements CsaEntity{
    private String bestelNR;
    private int klantID;
    private int contractID; //valt nog te bekijken (misschien beter aanbieding?)
    private boolean afgehaald;
    //private Map<Product,Integer> producten;                    //was dit voor een bepaalde reden? het gaf errors met de json omzetting
    private Product[] producten;

    public WekelijkseBestelling(String bestelNR, int klantID, int contractID) {
        this.bestelNR = bestelNR;
        this.klantID = klantID;
        this.contractID = contractID;
    }

    public String getBestelNR() {
        return bestelNR;
    }

    public void setBestelNR(String bestelNR) {
        this.bestelNR = bestelNR;
    }

    public int getKlantID() {
        return klantID;
    }

    public void setKlantID(int klantID) {
        this.klantID = klantID;
    }

    public int getContractID() {
        return contractID;
    }

    public void setContractID(int contractID) {
        this.contractID = contractID;
    }

    public Product[] getProducten() {
        return producten;
    }

    public void setProducten(Product[] producten) {
        this.producten = producten;
    }

    public boolean isAfgehaald() {
        return afgehaald;
    }

    public void setAfgehaald(boolean afgehaald) {
        this.afgehaald = afgehaald;
    }
}






































