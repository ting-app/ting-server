package ting.repository.extend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import ting.entity.TingPractice;
import ting.repository.TingPracticeRepository;

import java.util.List;

/**
 * The extended repository that manipulates ting practice entities.
 */
@Repository
public class TingPracticeRepositoryExtend {
    @Autowired
    private TingPracticeRepository tingPracticeRepository;

    /**
     * Find ting practices.
     *
     * @param createdBy Who creates the ting practice
     * @param page      The page number
     * @param pageSize  Count of ting practices returned in each page
     * @return List of {@link ting.entity.TingPractice}
     */
    public List<TingPractice> findAll(Long createdBy, Integer page, Integer pageSize) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withIgnoreNullValues();
        TingPractice tingPractice = new TingPractice();

        if (createdBy != null) {
            tingPractice.setCreatedBy(createdBy);
        }

        Example<TingPractice> example = Example.of(tingPractice, exampleMatcher);
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        if (page != null && page > 0 && pageSize != null && pageSize > 0) {
            Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

            return tingPracticeRepository.findAll(example, pageable).getContent();
        } else {
            return tingPracticeRepository.findAll(example, sort);
        }
    }
}
