package ting.service;

import org.springframework.stereotype.Service;
import ting.entity.Program;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProgramService {
    @PersistenceContext
    private EntityManager entityManager;

    public List<Program> findAll(Integer language) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Program> criteriaQuery = criteriaBuilder.createQuery(Program.class);
        Root<Program> programRoot = criteriaQuery.from(Program.class);
        List<Predicate> predicates = new ArrayList<>();

        if (language != null && language > 0) {
            predicates.add(criteriaBuilder.equal(programRoot.get("language"), language));
        }

        criteriaQuery.select(programRoot)
                .where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
