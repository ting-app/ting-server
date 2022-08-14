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
import ting.dto.TingPracticeDto;
import ting.dto.UserDto;
import ting.entity.Ting;
import ting.entity.TingPractice;
import ting.repository.TingPracticeRepository;
import ting.repository.TingRepository;

import javax.validation.Valid;
import java.time.Instant;

@RestController
public class TingPracticeController extends BaseController {
    @Autowired
    private TingRepository tingRepository;

    @Autowired
    private TingPracticeRepository tingPracticeRepository;

    @PostMapping("/tingPractices")
    @LoginRequired
    public ResponseEntity<?> createTingPractice(
            @Valid @RequestBody TingPracticeDto tingPracticeDto, @Me UserDto me) {
        Ting ting = tingRepository.findById(tingPracticeDto.getTingId()).orElse(null);

        if (ting == null) {
            return new ResponseEntity<>(new ResponseError("听力不存在"), HttpStatus.NOT_FOUND);
        }

        TingPractice tingPractice = new TingPractice();
        tingPractice.setUserId(me.getId());
        tingPractice.setTingId(tingPracticeDto.getTingId());
        tingPractice.setContent(tingPracticeDto.getContent());
        tingPractice.setScore(tingPracticeDto.getScore());
        tingPractice.setTimeCostInSeconds(tingPracticeDto.getTimeCostInSeconds());
        tingPractice.setCreatedAt(Instant.now());

        tingPracticeRepository.save(tingPractice);

        tingPracticeDto.setId(tingPractice.getId());

        return new ResponseEntity<>(tingPracticeDto, HttpStatus.OK);
    }
}
