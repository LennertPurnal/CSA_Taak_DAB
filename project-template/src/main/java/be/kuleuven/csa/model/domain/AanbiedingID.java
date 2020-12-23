package be.kuleuven.csa.model.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class AanbiedingID implements Serializable {
    @Column(name = "ondernemingsNR")
    private int landbouwbedrijf;
    @Column(name = "pakketnaam")
    private String pakket;

    public AanbiedingID() {
    }

    public int getLandbouwbedrijf() {
        return landbouwbedrijf;
    }

    public void setLandbouwbedrijf(int landbouwbedrijf) {
        this.landbouwbedrijf = landbouwbedrijf;
    }

    public String getPakket() {
        return pakket;
    }

    public void setPakket(String pakket) {
        this.pakket = pakket;
    }
}
