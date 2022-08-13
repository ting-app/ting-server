package ting.controller;

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
import ting.annotation.LoginRequired;
import ting.annotation.Me;
import ting.dto.ResponseError;
import ting.dto.TingDto;
import ting.dto.UserDto;
import ting.entity.Program;
import ting.entity.Ting;
import ting.repository.ProgramRepository;
import ting.repository.TingRepository;
import ting.service.AzureBlobStorageService;
import ting.service.BlobSas;

import javax.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TingController extends BaseController {
    @Autowired
    private TingRepository tingRepository;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private AzureBlobStorageService azureBlobStorageService;

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

    @DeleteMapping("/tings/{id}")
    @LoginRequired
    public ResponseEntity<?> deleteTing(@PathVariable long id, @Me UserDto me) {
        Ting ting = tingRepository.findById(id).orElse(null);

        if (ting == null) {
            return new ResponseEntity<>(new ResponseError("听力不存在"), HttpStatus.NOT_FOUND);
        }

        Program program = programRepository.findById(ting.getProgramId()).orElse(null);

        if (program != null && me.getId() != program.getCreatedBy()) {
            return new ResponseEntity<>(new ResponseError("节目创建人与当前用户不一致"), HttpStatus.FORBIDDEN);
        }

        tingRepository.delete(ting);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/tings/{id}")
    @LoginRequired
    public ResponseEntity<?> updateTing(@PathVariable long id, @Valid @RequestBody TingDto tingDto, @Me UserDto me) {
        Ting ting = tingRepository.findById(id).orElse(null);

        if (ting == null) {
            return new ResponseEntity<>(new ResponseError("听力不存在"), HttpStatus.NOT_FOUND);
        }

        Program program = programRepository.findById(ting.getProgramId()).orElse(null);

        if (program != null && me.getId() != program.getCreatedBy()) {
            return new ResponseEntity<>(new ResponseError("节目创建人与当前用户不一致"), HttpStatus.FORBIDDEN);
        }

        if (tingDto.getProgramId() != ting.getProgramId()) {
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

    @GetMapping("/tings/{id}")
    public ResponseEntity<?> getTing(@PathVariable long id) {
        Ting ting = tingRepository.findById(id).orElse(null);

        if (ting == null) {
            return new ResponseEntity<>(new ResponseError("听力不存在"), HttpStatus.NOT_FOUND);
        }

        BlobSas blobSas = azureBlobStorageService.generateSas(AzureBlobStorageService.READ_PERMISSION);
        String audioUrl = ting.getAudioUrl() + "?" + blobSas.getSas();

        TingDto tingDto = new TingDto();
        tingDto.setId(ting.getId());
        tingDto.setProgramId(ting.getProgramId());
        tingDto.setTitle(ting.getTitle());
        tingDto.setDescription(ting.getDescription());
        tingDto.setContent(ting.getContent());
        tingDto.setAudioUrl(audioUrl);
        tingDto.setCreatedAt(ting.getCreatedAt());
        tingDto.setUpdatedAt(ting.getUpdatedAt());

        return new ResponseEntity<>(tingDto, HttpStatus.OK);
    }

    @GetMapping("/tings")
    public ResponseEntity<?> getTings(@RequestParam long programId) {
        Program program = programRepository.findById(programId).orElse(null);

        if (program == null) {
            return new ResponseEntity<>(new ResponseError("节目不存在"), HttpStatus.NOT_FOUND);
        }

        List<Ting> tings = tingRepository.findByProgramId(programId);
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

        return new ResponseEntity<>(tingDtos, HttpStatus.OK);
    }
}
