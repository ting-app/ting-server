package ting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ting.annotation.LoginRequired;
import ting.annotation.Me;
import ting.dto.ProgramDto;
import ting.dto.ResponseError;
import ting.dto.UserDto;
import ting.entity.Program;
import ting.repository.ProgramRepository;
import ting.service.ProgramService;

import javax.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ProgramController extends BaseController {
    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private ProgramService programService;

    @GetMapping("/programs")
    public List<ProgramDto> getPrograms(@RequestParam(required = false) Integer language, @RequestParam(required = false) Integer createdBy) {
        List<Program> programs = programService.findAll(language, createdBy);
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

        return programDtos;
    }

    @PostMapping("/programs")
    @LoginRequired
    public ResponseEntity<ProgramDto> createProgram(@Valid @RequestBody ProgramDto programDto, @Me UserDto me) {
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

        programService.deleteById(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
