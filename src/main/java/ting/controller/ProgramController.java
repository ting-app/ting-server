package ting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ting.annotation.LoginRequired;
import ting.annotation.Me;
import ting.dto.ProgramDto;
import ting.dto.UserDto;
import ting.entity.Program;
import ting.repository.ProgramRepository;

import javax.validation.Valid;
import java.time.Instant;

@RestController
public class ProgramController extends BaseController {
    @Autowired
    private ProgramRepository programRepository;

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
    public ResponseEntity<ProgramDto> getProgram(@PathVariable long id) {
        Program program = programRepository.findById(id).orElse(null);

        if (program == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
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
}
