package ting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ting.annotation.LoginRequired;
import ting.annotation.Me;
import ting.dto.ResponseError;
import ting.dto.TingDto;
import ting.dto.UserDto;
import ting.entity.Program;
import ting.entity.Ting;
import ting.repository.ProgramRepository;
import ting.repository.TingRepository;

import javax.validation.Valid;
import java.time.Instant;

@RestController
public class TingController extends BaseController {
    @Autowired
    private TingRepository tingRepository;

    @Autowired
    private ProgramRepository programRepository;

    @PostMapping("/tings")
    @LoginRequired
    public ResponseEntity<?> createTing(@Valid @RequestBody TingDto tingDto, @Me UserDto me) {
        Program program = programRepository.findById(tingDto.getProgramId()).orElse(null);

        if (program == null) {
            return new ResponseEntity<>(new ResponseError("节目不存在"), HttpStatus.BAD_REQUEST);
        }

        if (me.getId() != program.getCreatedBy()) {
            return new ResponseEntity<>(new ResponseError("节目创建人与当前用户不一致"), HttpStatus.FORBIDDEN);
        }

        Instant now = Instant.now();
        Ting ting = new Ting();
        ting.setProgramId(program.getId());
        ting.setTitle(tingDto.getTitle());
        ting.setDescription(tingDto.getDescription());
        ting.setAudioUrl(tingDto.getAudioUrl());
        ting.setContent(tingDto.getContent());
        ting.setCreatedAt(now);
        ting.setUpdatedAt(now);

        tingRepository.save(ting);

        tingDto.setId(ting.getId());
        tingDto.setCreatedAt(now);
        tingDto.setUpdatedAt(now);

        return new ResponseEntity<>(tingDto, HttpStatus.CREATED);
    }
}
