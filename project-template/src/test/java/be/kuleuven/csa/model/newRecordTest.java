package be.kuleuven.csa.model;

import be.kuleuven.csa.model.databaseConn.CsaDatabaseRepo;
import be.kuleuven.csa.model.domain.Aanbieding;
import be.kuleuven.csa.model.domain.Klant;
import be.kuleuven.csa.model.domain.Landbouwbedrijf;
import be.kuleuven.csa.model.domain.Pakket;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class newRecordTest {

    private EntityManagerFactory factory;
    private EntityManager entityManager;
    private CsaDatabaseRepo databaseRepo;

    @Before
    public void setUp(){
        this.factory = Persistence.createEntityManagerFactory("be.kuleuven.csa.model.domain");
        this.entityManager = factory.createEntityManager();
        this.databaseRepo = new CsaDatabaseRepo(entityManager);
    }

    @After
    public void tearDown(){
        factory.close();
    }

    @Test
    public void saveNewKlantTest(){
        Klant klant = new Klant("testklant", "stadofdorp", 666, "straat", 21, "België");
        databaseRepo.saveNewRecord(klant);

        Assert.assertTrue("klant heeft id gekregen", klant.getKlantID() > 0);
        entityManager.clear();
    }

    @Test
    public void saveNewLandbouwbedrijfTest(){
        Landbouwbedrijf landbouwbedrijf = new Landbouwbedrijf(111111, "testbedrijf", "stadofdorp", 666);
        databaseRepo.saveNewRecord(landbouwbedrijf);
        entityManager.clear();
    }

    @Test
    public void savenewAanbiedingTest(){
        Pakket pakket = new Pakket("medium", 2, 2, "korte beschrijving");
        Landbouwbedrijf landbouwbedrijf = new Landbouwbedrijf(1, "testbedrijf", "stadofdorp", 666);
        //databaseRepo.saveNewRecord(landbouwbedrijf);
        //databaseRepo.saveNewRecord(pakket);


        Aanbieding aanbieding = new Aanbieding(pakket, landbouwbedrijf, 100);
        pakket.voegAanbiedingToe(aanbieding);
        landbouwbedrijf.voegAanbiedingToe(aanbieding);
        databaseRepo.saveNewRecord(aanbieding);
        entityManager.clear();
    }
}
