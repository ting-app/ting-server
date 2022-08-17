package ting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ting.entity.Program;
import ting.repository.ProgramRepository;
import ting.repository.TingRepository;

import java.util.List;

/**
 * The service that manipulates program entities.
 */
@Service
public class ProgramService {
    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private TingRepository tingRepository;

    /**
     * Find programs by language/createdBy.
     *
     * @param language  Language of the Program
     * @param createdBy Who creates the program
     * @param page      The page number
     * @param pageSize  Count of programs returned in each page
     * @return List of {@link ting.entity.Program}
     */
    public List<Program> findAll(
            Integer language, Integer createdBy, Integer page, Integer pageSize) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnorePaths("id");
        Program program = new Program();

        if (language != null && language > 0) {
            program.setLanguage(language);
        } else {
            exampleMatcher = exampleMatcher.withIgnorePaths("language");
        }

        if (createdBy != null) {
            program.setCreatedBy(createdBy);
        } else {
            exampleMatcher = exampleMatcher.withIgnorePaths("createdBy");
        }

        Example<Program> example = Example.of(program, exampleMatcher);

        if (page != null && page > 0 && pageSize != null && pageSize > 0) {
            Pageable pageable = PageRequest.of(page - 1, pageSize);

            return programRepository.findAll(example, pageable).getContent();
        } else {
            return programRepository.findAll(example);
        }
    }

    /**
     * Count programs by language/createdBy.
     *
     * @param language  Language of the Program
     * @param createdBy Who creates the program
     * @return Count of programs
     */
    public long count(Integer language, Integer createdBy) {
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnorePaths("id");
        Program program = new Program();

        if (language != null && language > 0) {
            program.setLanguage(language);
        } else {
            exampleMatcher = exampleMatcher.withIgnorePaths("language");
        }

        if (createdBy != null) {
            program.setCreatedBy(createdBy);
        } else {
            exampleMatcher = exampleMatcher.withIgnorePaths("createdBy");
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
