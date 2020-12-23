package be.kuleuven.csa.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Product implements CsaEntity{
    @Id
    private String naam;
    @Column
    private String beschrijving;

    public Product(String naam) {
        this.naam = naam;
    }
}
