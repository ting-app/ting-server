package ting.controller;

import org.apache.commons.lang3.StringUtils;
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
    public Response<TingDto> createTing(@RequestBody TingDto tingDto, @Me UserDto me) {
        if (tingDto == null) {
            return new Response<>(new ResponseError("标题不能为空"));
        }

        if (StringUtils.isBlank(tingDto.getTitle())) {
            return new Response<>(new ResponseError("标题不能为空"));
        }

        if (tingDto.getTitle().length() > 100) {
            return new Response<>(new ResponseError("标题不能超过100个字符"));
        }

        if (!StringUtils.isBlank(tingDto.getDescription()) && tingDto.getDescription().length() > 200) {
            return new Response<>(new ResponseError("描述不能超过200个字符"));
        }

        if (StringUtils.isBlank(tingDto.getAudioUrl())) {
            return new Response<>(new ResponseError("资源文件不能为空"));
        }

        if (StringUtils.isBlank(tingDto.getContent())) {
            return new Response<>(new ResponseError("内容不能为空"));
        }

        if (tingDto.getContent().length() > 2000) {
            return new Response<>(new ResponseError("内容不能超过2000个字符"));
        }

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
