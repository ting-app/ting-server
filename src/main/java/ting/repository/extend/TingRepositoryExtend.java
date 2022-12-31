package ting.repository.extend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import ting.entity.Ting;
import ting.repository.TingRepository;

import java.util.List;

@Repository
public class TingRepositoryExtend {
    @Autowired
    private TingRepository tingRepository;

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
