package dekiru.ting.controller;

import dekiru.ting.annotation.Me;
import dekiru.ting.dto.UserDto;
import dekiru.ting.entity.BaseEntity;
import dekiru.ting.entity.Ting;
import dekiru.ting.entity.TingPractice;
import dekiru.ting.repository.TingPracticeRepository;
import dekiru.ting.repository.TingRepository;
import dekiru.ting.repository.extend.TingPracticeRepositoryExtend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import dekiru.ting.Constant;
import dekiru.ting.annotation.LoginRequired;
import dekiru.ting.dto.ResponseError;
import dekiru.ting.dto.TingPracticeDto;

import javax.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The api routes for ting practices.
 */
@RestController
public class TingPracticeController extends BaseController {
    @Autowired
    private TingRepository tingRepository;

    @Autowired
    private TingPracticeRepository tingPracticeRepository;

    @Autowired
    private TingPracticeRepositoryExtend tingPracticeRepositoryExtend;

    /**
     * Create a ting practice.
     *
     * @param tingPracticeDto The request entity to create a ting practice
     * @param me              Current user
     * @return Created new ting practice
     */
    @PostMapping("/tingPractices")
    @LoginRequired
    public ResponseEntity<?> createTingPractice(
            @Valid @RequestBody TingPracticeDto tingPracticeDto, @Me UserDto me) {
        Ting ting = tingRepository.findById(tingPracticeDto.getTingId()).orElse(null);

        if (ting == null) {
            return new ResponseEntity<>(new ResponseError("听力不存在"), HttpStatus.NOT_FOUND);
        }

        TingPractice tingPractice = new TingPractice();
        tingPractice.setCreatedBy(me.getId());
        tingPractice.setTingId(tingPracticeDto.getTingId());
        tingPractice.setContent(tingPracticeDto.getContent());
        tingPractice.setScore(tingPracticeDto.getScore());
        tingPractice.setTimeCostInSeconds(tingPracticeDto.getTimeCostInSeconds());
        tingPractice.setCreatedAt(Instant.now());

        tingPracticeRepository.save(tingPractice);

        tingPracticeDto.setId(tingPractice.getId());

        return new ResponseEntity<>(tingPracticeDto, HttpStatus.CREATED);
    }

    /**
     * Get ting practices.
     *
     * @param createdBy Who creates the ting practice
     * @param page      The page number
     * @param pageSize  Count of ting practices returned in each page
     * @return List of {@link TingPracticeDto}
     */
    @GetMapping("/tingPractices")
    @LoginRequired
    public ResponseEntity<?> getTingPractices(
            @RequestParam long createdBy, @RequestParam int page, @RequestParam int pageSize) {
        if (page <= 0) {
            return new ResponseEntity<>(new ResponseError("page 参数无效"), HttpStatus.BAD_REQUEST);
        }

        if (pageSize <= 0) {
            return new ResponseEntity<>(
                    new ResponseError("pageSize 参数无效"), HttpStatus.BAD_REQUEST);
        } else if (pageSize > Constant.MAX_PAGE_SIZE) {
            return new ResponseEntity<>(
                    new ResponseError(
                            String.format("pageSize 超过最大值 %d", Constant.MAX_PAGE_SIZE)),
                    HttpStatus.BAD_REQUEST);
        }

        List<TingPractice> tingPractices = tingPracticeRepositoryExtend.findAll(
                createdBy, page, pageSize);
        List<TingPracticeDto> tingPracticeDtos = tingPractices.stream()
                .map(tingPractice -> {
                    TingPracticeDto tingPracticeDto = new TingPracticeDto();
                    tingPracticeDto.setId(tingPractice.getId());
                    tingPracticeDto.setCreatedBy(tingPractice.getCreatedBy());
                    tingPracticeDto.setTingId(tingPractice.getTingId());
                    tingPracticeDto.setContent(tingPractice.getContent());
                    tingPracticeDto.setScore(tingPractice.getScore());
                    tingPracticeDto.setTimeCostInSeconds(tingPractice.getTimeCostInSeconds());
                    tingPracticeDto.setCreatedAt(tingPractice.getCreatedAt());

                    return tingPracticeDto;
                })
                .collect(Collectors.toList());
        List<Long> tingIds = tingPracticeDtos.stream()
                .map(TingPracticeDto::getTingId)
                .distinct()
                .toList();
        List<Ting> tings = tingRepository.findByIdIn(tingIds);
        Map<Long, Ting> tingMap = tings.stream()
                .collect(Collectors.toMap(BaseEntity::getId, it -> it));

        for (TingPracticeDto tingPracticeDto : tingPracticeDtos) {
            Ting ting = tingMap.get(tingPracticeDto.getTingId());

            if (ting != null) {
                tingPracticeDto.setTingTitle(ting.getTitle());
            } else {
                tingPracticeDto.setTingTitle("听力已删除");
            }
        }

        return new ResponseEntity<>(tingPracticeDtos, HttpStatus.OK);
    }
}
