package be.kuleuven.csa.model.databaseConn;

import be.kuleuven.csa.model.domain.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.List;
import java.util.function.Predicate;

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

    public void deleteRecord(CsaEntity record){
        entityManager.getTransaction().begin();
        entityManager.remove(record);
        CsaDatabaseConn.getDatabaseConn().getCsaRepo().flushAndClear();
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

    public List<Contract> getContracten(Contract filter){
        List<Contract> resultsList;
        var criteriabuilder = entityManager.getCriteriaBuilder();
        var query = criteriabuilder.createQuery(Contract.class);
        var root = query.from(Contract.class);
        if (filter == null){
            var all = query.select(root);
            var alleContractenQuery = entityManager.createQuery(all);
            resultsList = alleContractenQuery.getResultList();
        } else {
            var subqueryklanten = query.subquery(Klant.class);
            var klantRoot = subqueryklanten.from(Klant.class);
            subqueryklanten.select(klantRoot).where(criteriabuilder.like(klantRoot.get("naam"),"%"+filter.getKlant().getNaam()+"%"));

            var subquerybedrijven = query.subquery(Landbouwbedrijf.class);
            var bedrijfRoot = subquerybedrijven.from(Landbouwbedrijf.class);
            subquerybedrijven.select(bedrijfRoot).where(criteriabuilder.like(bedrijfRoot.get("naam"), "%"+filter.getLandbouwbedrijf().getNaam()+"%"));

            var q = query.select(root).where(criteriabuilder.in(root.get("klant")).value(subqueryklanten),criteriabuilder.in(root.get("landbouwbedrijf")).value(subquerybedrijven));
            resultsList = entityManager.createQuery(q).getResultList();
        }
        return resultsList;
    }

    public List<Aanbieding> getAanbiedingen(Aanbieding filter, int minPrijs, int maxPrijs){
        List<Aanbieding> resultsList;
        var criteriabuilder = entityManager.getCriteriaBuilder();
        var query = criteriabuilder.createQuery(Aanbieding.class);
        var root = query.from(Aanbieding.class);
        if (filter == null){
            var cq = query.select(root).where(criteriabuilder.between(root.get("prijs"),minPrijs, maxPrijs));
            resultsList = entityManager.createQuery(cq).getResultList();
        } else {
            var subqueryPakketen = query.subquery(Pakket.class);
            var pakketRoot = subqueryPakketen.from(Pakket.class);
            subqueryPakketen.select(pakketRoot).where(criteriabuilder.like(pakketRoot.get("pakketnaam"),"%"+filter.getPakket().getPakketnaam()+"%"));

            var subquerybedrijven = query.subquery(Landbouwbedrijf.class);
            var bedrijfRoot = subquerybedrijven.from(Landbouwbedrijf.class);
            subquerybedrijven.select(bedrijfRoot).where(criteriabuilder.like(bedrijfRoot.get("naam"), "%"+filter.getLandbouwbedrijf().getNaam()+"%"));

            var cq = query.select(root).where(criteriabuilder.in(root.get("pakket")).value(subqueryPakketen)
                    ,criteriabuilder.in(root.get("landbouwbedrijf")).value(subquerybedrijven)
                    ,criteriabuilder.between(root.get("prijs"),minPrijs,maxPrijs));
            resultsList = entityManager.createQuery(cq).getResultList();
        }
        return resultsList;
    }

    public List<Pakket> getPakketen(Pakket filter){
        List<Pakket> resultsList;
        var criteriabuilder = entityManager.getCriteriaBuilder();
        var query = criteriabuilder.createQuery(Pakket.class);
        var root = query.from(Pakket.class);
        if (filter == null){
            var cq = query.select(root);
            resultsList = entityManager.createQuery(cq).getResultList();
        } else {
            var cq = query.select(root).where(criteriabuilder.like(root.get("pakketnaam"),"%"+filter.getPakketnaam()+"%"));
            resultsList = entityManager.createQuery(cq).getResultList();
        }
        return resultsList;
    }

    public void flushAndClear(){
        entityManager.flush();
        entityManager.clear();
    }
}
