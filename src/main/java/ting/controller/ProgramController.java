package ting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ting.Constant;
import ting.annotation.LoginRequired;
import ting.annotation.Me;
import ting.dto.ProgramDto;
import ting.dto.ResponseError;
import ting.dto.UserDto;
import ting.entity.Program;
import ting.repository.ProgramRepository;
import ting.repository.extend.ProgramRepositoryExtend;

import javax.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The api routes for programs.
 */
@RestController
public class ProgramController extends BaseController {
    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private ProgramRepositoryExtend programRepositoryExtend;

    /**
     * Get programs.
     *
     * @param language  The language of the program
     * @param createdBy Who creates the program
     * @param page      The page number
     * @param pageSize  Count of programs returned in each page
     * @return List of {@link ting.dto.ProgramDto}
     */
    @GetMapping("/programs")
    public ResponseEntity<?> getPrograms(
            @RequestParam(required = false) Integer language,
            @RequestParam(required = false) Integer createdBy,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        if (language != null && language <= 0) {
            return new ResponseEntity<>(
                    new ResponseError("language 参数无效"), HttpStatus.BAD_REQUEST);
        }

        if (page != null && page <= 0) {
            return new ResponseEntity<>(new ResponseError("page 参数无效"), HttpStatus.BAD_REQUEST);
        }

        if (pageSize != null) {
            if (pageSize <= 0) {
                return new ResponseEntity<>(
                        new ResponseError("pageSize 参数无效"), HttpStatus.BAD_REQUEST);
            } else if (pageSize > Constant.MAX_PAGE_SIZE) {
                return new ResponseEntity<>(
                        new ResponseError(
                                String.format("pageSize 超过最大值 %d", Constant.MAX_PAGE_SIZE)),
                        HttpStatus.BAD_REQUEST);
            }
        }

        List<Program> programs = programRepositoryExtend.findAll(
                language, createdBy, page, pageSize);
        List<ProgramDto> programDtos = programs.stream()
                .map(program -> {
                    ProgramDto programDto = new ProgramDto();
                    programDto.setId(program.getId());
                    programDto.setTitle(program.getTitle());
                    programDto.setDescription(program.getDescription());
                    programDto.setLanguage(program.getLanguage());
                    programDto.setCreatedAt(program.getCreatedAt());
                    programDto.setUpdatedAt(program.getUpdatedAt());

                    return programDto;
                })
                .collect(Collectors.toList());

        return new ResponseEntity<>(programDtos, HttpStatus.OK);
    }

    /**
     * Create a new program.
     *
     * @param programDto The request entity to create a new program
     * @param me         Current user
     * @return Created new program
     */
    @PostMapping("/programs")
    @LoginRequired
    public ResponseEntity<ProgramDto> createProgram(
            @Valid @RequestBody ProgramDto programDto, @Me UserDto me) {
        Instant now = Instant.now();
        Program program = new Program();
        program.setTitle(programDto.getTitle());
        program.setLanguage(programDto.getLanguage());
        program.setDescription(programDto.getDescription());
        program.setCreatedBy(me.getId());
        program.setCreatedAt(now);
        program.setUpdatedAt(now);

        programRepository.save(program);

        programDto.setId(program.getId());
        programDto.setCreatedBy(me.getId());
        programDto.setCreatedAt(now);
        programDto.setUpdatedAt(now);

        return new ResponseEntity<>(programDto, HttpStatus.CREATED);
    }

    /**
     * Get program id.
     *
     * @param id The id of program
     * @return {@link ting.dto.ProgramDto}
     */
    @GetMapping("/programs/{id}")
    public ResponseEntity<?> getProgram(@PathVariable long id) {
        Program program = programRepository.findById(id).orElse(null);

        if (program == null) {
            return new ResponseEntity<>(new ResponseError("节目不存在"), HttpStatus.NOT_FOUND);
        }

        ProgramDto programDto = new ProgramDto();
        programDto.setId(program.getId());
        programDto.setTitle(program.getTitle());
        programDto.setDescription(program.getDescription());
        programDto.setLanguage(program.getLanguage());
        programDto.setCreatedBy(program.getCreatedBy());
        programDto.setCreatedAt(program.getCreatedAt());

        return new ResponseEntity<>(programDto, HttpStatus.OK);
    }

    /**
     * Delete program by id.
     *
     * @param id The id of program
     * @param me Current user
     * @return {@link java.lang.Void}
     */
    @DeleteMapping("/programs/{id}")
    @LoginRequired
    public ResponseEntity<?> deleteProgram(@PathVariable long id, @Me UserDto me) {
        Program program = programRepository.findById(id).orElse(null);

        if (program == null) {
            return new ResponseEntity<>(new ResponseError("节目不存在"), HttpStatus.NOT_FOUND);
        }

        if (me.getId() != program.getCreatedBy()) {
            return new ResponseEntity<>(new ResponseError("节目创建人与当前用户不一致"), HttpStatus.FORBIDDEN);
        }

        programRepositoryExtend.deleteById(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Update program by id.
     *
     * @param id         The id of program
     * @param programDto The request entity to update the program
     * @param me         Current user
     * @return The updated program
     */
    @PutMapping("/programs/{id}")
    @LoginRequired
    public ResponseEntity<?> updateProgram(
            @PathVariable long id, @Valid @RequestBody ProgramDto programDto, @Me UserDto me) {
        Program program = programRepository.findById(id).orElse(null);

        if (program == null) {
            return new ResponseEntity<>(new ResponseError("节目不存在"), HttpStatus.NOT_FOUND);
        }

        if (me.getId() != program.getCreatedBy()) {
            return new ResponseEntity<>(new ResponseError("节目创建人与当前用户不一致"), HttpStatus.FORBIDDEN);
        }

        Instant now = Instant.now();
        program.setTitle(programDto.getTitle());
        program.setLanguage(programDto.getLanguage());
        program.setDescription(programDto.getDescription());
        program.setUpdatedAt(now);

        programRepository.save(program);

        programDto.setId(id);
        programDto.setUpdatedAt(now);

        return new ResponseEntity<>(programDto, HttpStatus.OK);
    }
}
