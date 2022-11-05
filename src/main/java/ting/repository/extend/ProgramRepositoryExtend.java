package ting.repository.extend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ting.entity.Program;
import ting.repository.ProgramRepository;
import ting.repository.TingRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The extended repository that manipulates program entities.
 */
@Repository
public class ProgramRepositoryExtend {
    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private TingRepository tingRepository;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Find all visible programs.
     *
     * @param language Language of the Program
     * @param page     The page number
     * @param pageSize Count of programs returned in each page
     * @return List of {@link ting.entity.Program}
     */
    public List<Program> findAllVisible(
            Integer language, int page, int pageSize) {
        if (page <= 0 || pageSize <= 0) {
            throw new IllegalArgumentException("Invalid page or pageSize");
        }

        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withIgnoreNullValues();
        Program program = new Program();
        program.setVisible(true);

        if (language != null && language > 0) {
            program.setLanguage(language);
        }

        Example<Program> example = Example.of(program, exampleMatcher);
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("updatedAt").descending());

        return programRepository.findAll(example, pageable).getContent();
    }

    /**
     * Find all programs that are visible to the specified user.
     *
     * @param language  Language of the Program
     * @param createdBy Who creates the program
     * @param page      The page number
     * @param pageSize  Count of programs returned in each page
     * @return List of {@link ting.entity.Program}
     */
    public List<Program> findAllVisibleTo(
            Integer language, long createdBy, int page, int pageSize) {
        if (page <= 0 || pageSize <= 0) {
            throw new IllegalArgumentException("Invalid page or pageSize");
        }

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Program> criteriaQuery = criteriaBuilder.createQuery(Program.class);
        Root<Program> programRoot = criteriaQuery.from(Program.class);
        List<Predicate> predicates = new ArrayList<>();

        if (language != null && language > 0) {
            predicates.add(criteriaBuilder.equal(programRoot.get("language"), language));
        }

        // The visible programs plus the programs created by createdBy
        predicates.add(criteriaBuilder.or(
                criteriaBuilder.equal(programRoot.get("visible"), true),
                criteriaBuilder.equal(programRoot.get("createdBy"), createdBy)
        ));

        criteriaQuery.select(programRoot)
                .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                .orderBy(Arrays.asList(criteriaBuilder.desc(programRoot.get("updatedAt"))));

        TypedQuery<Program> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }

    /**
     * Count programs by language/createdBy.
     *
     * @param language  Language of the Program
     * @param createdBy Who creates the program
     * @return Count of programs
     */
    public long count(Integer language, Long createdBy) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withIgnoreNullValues();
        Program program = new Program();

        if (language != null && language > 0) {
            program.setLanguage(language);
        }

        if (createdBy != null) {
            program.setCreatedBy(createdBy);
        }

        Example<Program> example = Example.of(program, exampleMatcher);

        return programRepository.count(example);
    }

    @Transactional
    public void deleteById(long id) {
        programRepository.deleteById(id);
        tingRepository.deleteByProgramId(id);
    }
}
