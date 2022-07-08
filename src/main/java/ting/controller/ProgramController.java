package ting.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ting.annotation.LoginRequired;
import ting.annotation.Me;
import ting.dto.Error;
import ting.dto.ProgramDto;
import ting.dto.Response;
import ting.dto.UserDto;
import ting.entity.Program;
import ting.repository.ProgramRepository;

import java.time.Instant;

@RestController
@RequestMapping("/programs")
public class ProgramController extends BaseController {
    @Autowired
    private ProgramRepository programRepository;

    @PostMapping
    @LoginRequired
    public Response<ProgramDto> createProgram(@RequestBody ProgramDto program, @Me UserDto me) {
        if (program == null) {
            return new Response<>(new Error("标题不能为空"));
        }

        if (StringUtils.isBlank(program.getTitle())) {
            return new Response<>(new Error("标题不能为空"));
        }

        if (program.getTitle().length() > 100) {
            return new Response<>(new Error("标题不能超过100个字符"));
        }

        if (StringUtils.isBlank(program.getDescription())) {
            return new Response<>(new Error("描述不能为空"));
        }

        if (program.getDescription().length() > 200) {
            return new Response<>(new Error("描述不能超过200个字符"));
        }

        Instant now = Instant.now();
        Program newProgram = new Program();
        newProgram.setTitle(program.getTitle());
        newProgram.setLanguage(program.getLanguage());
        newProgram.setDescription(program.getDescription());
        newProgram.setCreatedBy(me.getId());
        newProgram.setCreatedAt(now);

        programRepository.save(newProgram);

        program.setId(newProgram.getId());
        program.setCreatedBy(me.getId());
        program.setCreatedAt(now);

        return new Response<>(program);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<ProgramDto>> getProgram(@PathVariable long id) {
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

        return new ResponseEntity<>(new Response<>(programDto), HttpStatus.OK);
    }
}
