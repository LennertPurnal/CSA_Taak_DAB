package be.kuleuven.csa.model.domain;

import javax.persistence.EntityManager;

public class CsaDatabaseRepo {
    private final EntityManager entityManager;

    public CsaDatabaseRepo(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void saveNewKlant(Klant klant){
            entityManager.getTransaction().begin();
            entityManager.persist(klant);
            entityManager.getTransaction().commit();
    }
}
