package ting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ting.dto.ProgramDto;
import ting.dto.TingDto;
import ting.dto.UserLoginRequest;
import ting.entity.Program;
import ting.entity.Ting;
import ting.entity.User;
import ting.repository.ProgramRepository;
import ting.repository.TingRepository;
import ting.repository.UserRepository;
import ting.service.PasswordService;

import javax.servlet.http.Cookie;
import java.time.Instant;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public abstract class BaseTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private TingRepository tingRepository;

    @Autowired
    PasswordService passwordService;

    @Autowired
    protected MockMvc mockMvc;

    protected User currentUser;

    protected User otherUser;

    private String password = "123";

    protected static ObjectMapper objectMapper;

    @BeforeAll
    public static void beforeAll() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @BeforeEach
    public void beforeEach() {
        currentUser = new User();
        currentUser.setName(UUID.randomUUID().toString());
        currentUser.setEmail("example@example.com");
        currentUser.setEncryptedPassword(passwordService.encrypt(password));
        currentUser.setVerified(true);
        currentUser.setCreatedAt(Instant.now());

        otherUser = new User();
        otherUser.setName(UUID.randomUUID().toString());
        otherUser.setEmail("example@example.com");
        otherUser.setEncryptedPassword(passwordService.encrypt(password));
        otherUser.setVerified(true);
        otherUser.setCreatedAt(Instant.now());

        userRepository.save(currentUser);
        userRepository.save(otherUser);
    }

    protected Cookie[] login() throws Exception {
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setNameOrEmail(currentUser.getName());
        userLoginRequest.setPassword(password);

        ObjectMapper objectMapper = new ObjectMapper();
        String data = objectMapper.writeValueAsString(userLoginRequest);
        Cookie[] cookies = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(data))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getCookies();

        return cookies;
    }

    protected Program createMyProgram(int language, boolean visible) {
        Program program = new Program();
        program.setLanguage(language);
        program.setTitle(UUID.randomUUID().toString());
        program.setVisible(visible);
        program.setDescription("Description");
        program.setCreatedBy(currentUser.getId());
        program.setCreatedAt(Instant.now());
        program.setUpdatedAt(Instant.now());

        programRepository.save(program);

        return program;
    }

    protected Program createOtherUserProgram(int language, boolean visible) {
        Program program = new Program();
        program.setLanguage(language);
        program.setTitle(UUID.randomUUID().toString());
        program.setVisible(visible);
        program.setDescription("Description");
        program.setCreatedBy(otherUser.getId());
        program.setCreatedAt(Instant.now());
        program.setUpdatedAt(Instant.now());

        programRepository.save(program);

        return program;
    }

    protected ProgramDto createProgramDto(int language, boolean visible) {
        ProgramDto programDto = new ProgramDto();
        programDto.setLanguage(language);
        programDto.setTitle(UUID.randomUUID().toString());
        programDto.setVisible(visible);
        programDto.setDescription("Description");

        return programDto;
    }

    protected Ting createMyTing(long programId) {
        Instant now = Instant.now();
        Ting ting = new Ting();
        ting.setProgramId(programId);
        ting.setTitle("title");
        ting.setDescription("description");
        ting.setAudioUrl("audio");
        ting.setContent("content");
        ting.setCreatedAt(now);
        ting.setUpdatedAt(now);

        tingRepository.save(ting);

        return ting;
    }

    protected TingDto createTingDto(long programId) {
        TingDto tingDto = new TingDto();
        tingDto.setProgramId(programId);
        tingDto.setTitle("title");
        tingDto.setDescription("description");
        tingDto.setAudioUrl("audio");
        tingDto.setContent("content");

        return tingDto;
    }
}
