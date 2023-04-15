package dekiru.ting.controller;

import dekiru.ting.config.AwsS3Config;
import dekiru.ting.dto.TagDto;
import dekiru.ting.dto.UserDto;
import dekiru.ting.entity.BaseEntity;
import dekiru.ting.entity.Tag;
import dekiru.ting.entity.Ting;
import dekiru.ting.repository.ProgramRepository;
import dekiru.ting.repository.TagRepository;
import dekiru.ting.repository.TingRepository;
import dekiru.ting.repository.TingTagRepository;
import dekiru.ting.repository.extend.TingRepositoryExtend;
import org.apache.commons.collections4.CollectionUtils;
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
import dekiru.ting.Constant;
import dekiru.ting.annotation.LoginRequired;
import dekiru.ting.annotation.Me;
import dekiru.ting.dto.ResponseError;
import dekiru.ting.dto.TingDto;
import dekiru.ting.entity.Program;
import dekiru.ting.entity.TingTag;
import dekiru.ting.service.AwsS3Service;

import javax.validation.Valid;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The api routes for tings.
 */
@RestController
public class TingController extends BaseController {
    @Autowired
    private TingRepository tingRepository;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private TingRepositoryExtend tingRepositoryExtend;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TingTagRepository tingTagRepository;

    @Autowired
    private AwsS3Service awsS3Service;

    @Autowired
    private AwsS3Config awsS3Config;

    /**
     * Create a new ting.
     *
     * @param tingDto The request entity to create a new ting
     * @param me      Current user
     * @return Created new ting {@link TingDto}
     */
    @PostMapping("/tings")
    @LoginRequired
    public ResponseEntity<?> createTing(@Valid @RequestBody TingDto tingDto, @Me UserDto me) {
        Program program = programRepository.findById(tingDto.getProgramId()).orElse(null);

        if (program == null) {
            return new ResponseEntity<>(new ResponseError("节目不存在"), HttpStatus.BAD_REQUEST);
        }

        if (!Objects.equals(me.getId(), program.getCreatedBy())) {
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

    /**
     * Delete ting by id.
     *
     * @param id The id of ting
     * @param me Current user
     * @return {@link java.lang.Void}
     */
    @DeleteMapping("/tings/{id}")
    @LoginRequired
    public ResponseEntity<?> deleteTing(@PathVariable long id, @Me UserDto me) {
        Ting ting = tingRepository.findById(id).orElse(null);

        if (ting == null) {
            return new ResponseEntity<>(new ResponseError("听力不存在"), HttpStatus.NOT_FOUND);
        }

        Program program = programRepository.findById(ting.getProgramId()).orElse(null);

        if (program != null && !Objects.equals(me.getId(), program.getCreatedBy())) {
            return new ResponseEntity<>(new ResponseError("节目创建人与当前用户不一致"), HttpStatus.FORBIDDEN);
        }

        tingRepository.delete(ting);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Update ting by id.
     *
     * @param id      The id of ting
     * @param tingDto The request entity to update a ting
     * @param me      Current user
     * @return Updated ting {@link TingDto}
     */
    @PutMapping("/tings/{id}")
    @LoginRequired
    public ResponseEntity<?> updateTing(
            @PathVariable long id, @Valid @RequestBody TingDto tingDto, @Me UserDto me) {
        Ting ting = tingRepository.findById(id).orElse(null);

        if (ting == null) {
            return new ResponseEntity<>(new ResponseError("听力不存在"), HttpStatus.NOT_FOUND);
        }

        Program program = programRepository.findById(ting.getProgramId()).orElse(null);

        if (program != null && !Objects.equals(me.getId(), program.getCreatedBy())) {
            return new ResponseEntity<>(new ResponseError("节目创建人与当前用户不一致"), HttpStatus.FORBIDDEN);
        }

        if (!Objects.equals(tingDto.getProgramId(), ting.getProgramId())) {
            return new ResponseEntity<>(new ResponseError("听力所属的节目不一致"), HttpStatus.FORBIDDEN);
        }

        Instant now = Instant.now();
        ting.setTitle(tingDto.getTitle());
        ting.setDescription(tingDto.getDescription());
        ting.setContent(tingDto.getContent());
        ting.setAudioUrl(tingDto.getAudioUrl());
        ting.setUpdatedAt(now);

        tingRepository.save(ting);

        tingDto.setId(id);
        tingDto.setUpdatedAt(now);

        return new ResponseEntity<>(tingDto, HttpStatus.OK);
    }

    /**
     * Get ting by id.
     *
     * @param id The id of ting
     * @return {@link TingDto}
     */
    @GetMapping("/tings/{id}")
    public ResponseEntity<?> getTing(@PathVariable long id) {
        Ting ting = tingRepository.findById(id).orElse(null);

        if (ting == null) {
            return new ResponseEntity<>(new ResponseError("听力不存在"), HttpStatus.NOT_FOUND);
        }

        TingDto tingDto = new TingDto();
        tingDto.setId(ting.getId());
        tingDto.setProgramId(ting.getProgramId());
        tingDto.setTitle(ting.getTitle());
        tingDto.setDescription(ting.getDescription());
        tingDto.setContent(ting.getContent());
        tingDto.setCreatedAt(ting.getCreatedAt());
        tingDto.setUpdatedAt(ting.getUpdatedAt());

        if (ting.getAudioUrl().startsWith(
                String.format("https://%s", awsS3Config.getBucketName()))) {
            int index = ting.getAudioUrl().lastIndexOf('/');
            String fileName = ting.getAudioUrl().substring(index + 1);
            String presignedUrl = awsS3Service.getPresignedUrl(
                    AwsS3Service.READ_PERMISSION, fileName);

            tingDto.setAudioUrl(presignedUrl);
        } else {
            tingDto.setAudioUrl(ting.getAudioUrl());
        }

        setTags(Arrays.asList(tingDto));

        return new ResponseEntity<>(tingDto, HttpStatus.OK);
    }

    /**
     * Get tings by program id.
     *
     * @param programId The id of a program
     * @return List of {@link TingDto}
     */
    @GetMapping("/tings")
    public ResponseEntity<?> getTings(
            @RequestParam long programId, @RequestParam int page, @RequestParam int pageSize,
            @Me UserDto me) {
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

        Program program = programRepository.findById(programId).orElse(null);

        if (program == null) {
            return new ResponseEntity<>(new ResponseError("节目不存在"), HttpStatus.NOT_FOUND);
        }

        if (!program.getVisible()
                && (me == null || !Objects.equals(program.getCreatedBy(), me.getId()))) {
            return new ResponseEntity<>(new ResponseError("节目无权访问"), HttpStatus.FORBIDDEN);
        }

        List<Ting> tings = tingRepositoryExtend.findAll(programId, page, pageSize);
        List<TingDto> tingDtos = tings.stream()
                .map(ting -> {
                    TingDto tingDto = new TingDto();
                    tingDto.setId(ting.getId());
                    tingDto.setProgramId(ting.getProgramId());
                    tingDto.setTitle(ting.getTitle());
                    tingDto.setDescription(ting.getDescription());
                    tingDto.setAudioUrl(ting.getAudioUrl());
                    tingDto.setContent(ting.getContent());
                    tingDto.setCreatedAt(ting.getCreatedAt());
                    tingDto.setUpdatedAt(ting.getUpdatedAt());

                    return tingDto;
                })
                .collect(Collectors.toList());

        setTags(tingDtos);

        return new ResponseEntity<>(tingDtos, HttpStatus.OK);
    }

    /**
     * Get total count of tings by program id.
     *
     * @param programId The id of a program
     * @return Total count of tings
     */
    @GetMapping("/tings:count")
    public ResponseEntity<?> getTingsCount(@RequestParam long programId) {
        long count = tingRepository.countByProgramId(programId);

        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    /**
     * Set tags for ting.
     *
     * @param tingDtos List of {@link TingDto}
     */
    private void setTags(List<TingDto> tingDtos) {
        if (CollectionUtils.isEmpty(tingDtos)) {
            return;
        }

        List<Long> tingIds = tingDtos.stream()
                .map(TingDto::getId)
                .toList();
        List<TingTag> tingTags = tingTagRepository.findByTingIdIn(tingIds);

        if (CollectionUtils.isEmpty(tingTags)) {
            return;
        }

        List<Long> tagIds = tingTags.stream()
                .map(TingTag::getTagId)
                .toList();
        List<Tag> tags = tagRepository.findByIdIn(tagIds);

        if (CollectionUtils.isEmpty(tags)) {
            return;
        }

        Map<Long, Tag> tagMap = tags.stream()
                .collect(Collectors.toMap(BaseEntity::getId, it -> it));
        Map<Long, List<TingTag>> tingTagMap = tingTags.stream()
                .collect(Collectors.groupingBy(TingTag::getTingId));

        tingDtos.forEach(tingDto -> {
            List<TagDto> tagDtos = tingTagMap.getOrDefault(tingDto.getId(), new ArrayList<>())
                    .stream()
                    .map(TingTag::getTagId)
                    .map(tagMap::get)
                    .map(tag -> {
                        TagDto tagDto = new TagDto();
                        tagDto.setId(tag.getId());
                        tagDto.setName(tag.getName());

                        return tagDto;
                    })
                    .toList();

            tingDto.setTags(tagDtos);
        });
    }
}
