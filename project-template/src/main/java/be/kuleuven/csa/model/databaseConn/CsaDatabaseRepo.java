package be.kuleuven.csa.model.databaseConn;

import be.kuleuven.csa.model.domain.CsaEntity;
import be.kuleuven.csa.model.domain.Landbouwbedrijf;

import javax.persistence.EntityManager;
import java.util.List;

public class CsaDatabaseRepo {
    private final EntityManager entityManager;

    public CsaDatabaseRepo(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void persistRecord(CsaEntity record){
        entityManager.getTransaction().begin();
        entityManager.persist(record);
        entityManager.getTransaction().commit();
    }

    public List<Landbouwbedrijf> getLandbouwbedrijven(){
        var criteriabuilder = entityManager.getCriteriaBuilder();
        var query = criteriabuilder.createQuery(Landbouwbedrijf.class);
        var root = query.from(Landbouwbedrijf.class);
        var all = query.select(root);

        var alleboerderijen = entityManager.createQuery(all).setMaxResults(10);
        return alleboerderijen.getResultList();
    }
}
