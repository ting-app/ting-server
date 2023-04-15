package dekiru.ting.repository.extend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import dekiru.ting.entity.Ting;
import dekiru.ting.repository.TingRepository;

import java.util.List;

/**
 * The extended repository that manipulates ting entities.
 */
@Repository
public class TingRepositoryExtend {
    @Autowired
    private TingRepository tingRepository;

    /**
     * Find tings by program id.
     *
     * @param programId The id of program
     * @param page      The page number
     * @param pageSize  Count of programs returned in each page
     * @return List of {@link Ting}
     */
    public List<Ting> findAll(long programId, Integer page, Integer pageSize) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withIgnoreNullValues();
        Ting ting = new Ting();
        ting.setProgramId(programId);

        Example<Ting> example = Example.of(ting, exampleMatcher);
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        if (page != null && page > 0 && pageSize != null && pageSize > 0) {
            Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

            return tingRepository.findAll(example, pageable).getContent();
        } else {
            return tingRepository.findAll(example, sort);
        }
    }
}
