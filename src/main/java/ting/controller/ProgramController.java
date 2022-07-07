package ting.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ting.annotation.LoginRequired;
import ting.dto.ProgramDto;
import ting.dto.Response;

@RestController
@RequestMapping("/programs")
public class ProgramController extends BaseController {
    @PostMapping
    @LoginRequired
    public Response<ProgramDto> create(@RequestBody ProgramDto program) {
        return null;
    }
}
