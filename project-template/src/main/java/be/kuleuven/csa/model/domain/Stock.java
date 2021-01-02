package be.kuleuven.csa.model.domain;

import java.util.HashMap;
import java.util.Map;
public class Stock {

    private String _id;
    private Map<String,Integer> stock;
    private int ondernemingsNR;

    public Stock(int ondernemingsNR){
        this.ondernemingsNR = ondernemingsNR;
        this._id = "ONR" + ondernemingsNR;
        stock = new HashMap<String,Integer>();
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Map<String, Integer> getStock() {
        return stock;
    }

    public void setStock(Map<String, Integer> stock) {
        this.stock = stock;
    }

    public int getOndernemingsNR() {
        return ondernemingsNR;
    }

    public void setOndernemingsNR(int ondernemingsNR) {
        this.ondernemingsNR = ondernemingsNR;
    }
}
