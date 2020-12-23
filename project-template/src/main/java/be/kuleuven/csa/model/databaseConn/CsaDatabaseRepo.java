package be.kuleuven.csa.model.databaseConn;

import be.kuleuven.csa.model.domain.CsaEntity;

import javax.persistence.EntityManager;

public class CsaDatabaseRepo {
    private final EntityManager entityManager;

    public CsaDatabaseRepo(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void saveNewRecord(CsaEntity record){
            entityManager.getTransaction().begin();
            entityManager.persist(record);
            entityManager.getTransaction().commit();
    }
}
