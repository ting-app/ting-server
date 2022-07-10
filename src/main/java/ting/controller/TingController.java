package ting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ting.annotation.LoginRequired;
import ting.annotation.Me;
import ting.dto.Response;
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
@RequestMapping("/tings")
public class TingController extends BaseController {
    @Autowired
    private TingRepository tingRepository;

    @Autowired
    private ProgramRepository programRepository;

    @PostMapping
    @LoginRequired
    public Response<TingDto> createTing(@Valid @RequestBody TingDto tingDto, @Me UserDto me) {
        Program program = programRepository.findById(tingDto.getProgramId()).orElse(null);

        if (program == null) {
            return new Response<>(new ResponseError("节目不存在"));
        }

        if (me.getId() != program.getCreatedBy()) {
            return new Response<>(new ResponseError("节目创建人与当前用户不一致"));
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

        return new Response<>(tingDto);
    }
}
