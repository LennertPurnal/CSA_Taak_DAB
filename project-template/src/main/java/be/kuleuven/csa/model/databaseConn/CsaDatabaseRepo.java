package be.kuleuven.csa.model.databaseConn;

import be.kuleuven.csa.model.domain.CsaEntity;
import be.kuleuven.csa.model.domain.Klant;
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
        flushAndClear();
        entityManager.getTransaction().commit();
    }

    public void updateRecord(CsaEntity record){
        entityManager.getTransaction().begin();
        entityManager.merge(record);
        flushAndClear();
        entityManager.getTransaction().commit();
    }

    public List<Landbouwbedrijf> getLandbouwbedrijven(Landbouwbedrijf filter){
        List<Landbouwbedrijf> resultsList;
        var criteriabuilder = entityManager.getCriteriaBuilder();
        var query = criteriabuilder.createQuery(Landbouwbedrijf.class);
        var root = query.from(Landbouwbedrijf.class);

        if (filter == null){
            var all = query.select(root);
            var alleBoerderijenQuery = entityManager.createQuery(all);
            resultsList = alleBoerderijenQuery.getResultList();
        } else {
            var naamrestriction = criteriabuilder.like(root.get("naam"), "%"+filter.getNaam()+"%");
            var gemeenterestriction = criteriabuilder.like(root.get("gemeente"), "%"+filter.getGemeente()+"%");
            var landrestricrion = criteriabuilder.like(root.get("land"),"%"+filter.getLand()+"%");
            var filteredBoerderijen = query.where(criteriabuilder.and(naamrestriction, gemeenterestriction, landrestricrion));
            var filteredBoerderijenQuery = entityManager.createQuery(filteredBoerderijen);
            resultsList = filteredBoerderijenQuery.getResultList();
        }
        return resultsList;
    }

    public List<Klant> getKlanten(Klant filter){
        List<Klant> resultsList;
        var criteriabuilder = entityManager.getCriteriaBuilder();
        var query = criteriabuilder.createQuery(Klant.class);
        var root = query.from(Klant.class);

        if (filter == null){
            var all = query.select(root);
            var alleKlantenQuery = entityManager.createQuery(all);
            resultsList = alleKlantenQuery.getResultList();
        } else {
            var naamrestriction = criteriabuilder.like(root.get("naam"), "%"+filter.getNaam()+"%");
            var gemeenterestriction = criteriabuilder.like(root.get("gemeente"), "%"+filter.getGemeente()+"%");
            var straatrestriction = criteriabuilder.like(root.get("straat"), "%"+filter.getStraat()+"%");
            var landrestriction = criteriabuilder.like(root.get("land"),"%"+filter.getLand()+"%");
            var filteredBoerderijen = query.where(criteriabuilder.and(naamrestriction, gemeenterestriction, straatrestriction, landrestriction));
            var filteredBoerderijenQuery = entityManager.createQuery(filteredBoerderijen);
            resultsList = filteredBoerderijenQuery.getResultList();
        }
        return resultsList;
    }

    public void flushAndClear(){
        entityManager.flush();
        entityManager.clear();
    }
}
