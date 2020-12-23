package be.kuleuven.csa.model.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Landbouwbedrijf implements CsaEntity{
    @Id
    private int ondernemingsNR;
    @Column
    private String naam;
    @Column
    private String land;
    @Column
    private String gemeente;
    @Column
    private int postcode;

    @OneToMany(mappedBy = "landbouwbedrijf")
    private List<Aanbieding> aanbiedingen = new ArrayList<>();

    public Landbouwbedrijf(int ondernemingsNR, String naam, String gemeente, int postcode) {
        this.ondernemingsNR = ondernemingsNR;
        this.naam = naam;
        this.gemeente = gemeente;
        this.postcode = postcode;
    }

    public void voegAanbiedingToe(Aanbieding aanbieding){
        aanbiedingen.add(aanbieding);
    }

}
