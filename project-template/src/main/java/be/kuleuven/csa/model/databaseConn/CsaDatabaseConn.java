package be.kuleuven.csa.model.databaseConn;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class CsaDatabaseConn {
    private static final CsaDatabaseConn databaseConn = new CsaDatabaseConn();
    private static EntityManagerFactory sessionFactory;
    private EntityManager entityManager;
    private CsaDatabaseRepo csaRepo;


    private CsaDatabaseConn() {
    }

    public void setUp(){
        sessionFactory = Persistence.createEntityManagerFactory("be.kuleuven.csa.model.domain");
        entityManager = sessionFactory.createEntityManager();
        csaRepo = new CsaDatabaseRepo(entityManager);
    }

    public void tearDown(){
        entityManager.close();
        sessionFactory.close();
    }

    public static EntityManagerFactory getSessionFactory() {
        return sessionFactory;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public static CsaDatabaseConn getDatabaseConn() {
        return databaseConn;
    }

    public CsaDatabaseRepo getCsaRepo() {
        return csaRepo;
    }
}
