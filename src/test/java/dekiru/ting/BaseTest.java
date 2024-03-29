package dekiru.ting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dekiru.ting.repository.ProgramRepository;
import dekiru.ting.repository.TagRepository;
import dekiru.ting.repository.TingPracticeRepository;
import dekiru.ting.repository.TingRepository;
import dekiru.ting.repository.TingTagRepository;
import dekiru.ting.repository.UserRepository;
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
import dekiru.ting.dto.ProgramDto;
import dekiru.ting.dto.TingDto;
import dekiru.ting.dto.TingPracticeDto;
import dekiru.ting.dto.UserLoginRequest;
import dekiru.ting.dto.UserRegisterRequest;
import dekiru.ting.entity.Program;
import dekiru.ting.entity.Tag;
import dekiru.ting.entity.Ting;
import dekiru.ting.entity.TingPractice;
import dekiru.ting.entity.TingTag;
import dekiru.ting.entity.User;
import dekiru.ting.service.PasswordService;

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
    private TingPracticeRepository tingPracticeRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TingTagRepository tingTagRepository;

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

    protected Cookie[] login(User user, String password) throws Exception {
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setNameOrEmail(user.getName());
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

    protected Program createProgram(int language, boolean visible, long createdBy) {
        Program program = new Program();
        program.setLanguage(language);
        program.setTitle(UUID.randomUUID().toString());
        program.setVisible(visible);
        program.setDescription("Description");
        program.setCreatedBy(createdBy);
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

    protected Ting createTing(long programId) {
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

    protected Tag createTag(Long tingId) {
        Tag tag = new Tag();
        tag.setName(UUID.randomUUID().toString().substring(0, 20));

        tagRepository.save(tag);

        TingTag tingTag = new TingTag();
        tingTag.setTingId(tingId);
        tingTag.setTagId(tag.getId());

        tingTagRepository.save(tingTag);

        return tag;
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

    protected TingPracticeDto createTingPracticeDto(long tingId) {
        TingPracticeDto tingPracticeDto = new TingPracticeDto();
        tingPracticeDto.setTingId(tingId);
        tingPracticeDto.setContent("content");
        tingPracticeDto.setScore(0.99f);
        tingPracticeDto.setTimeCostInSeconds(10L);

        return tingPracticeDto;
    }

    protected TingPractice createMyTingPractice(long tingId) {
        TingPractice tingPractice = new TingPractice();
        tingPractice.setTingId(tingId);
        tingPractice.setContent("content");
        tingPractice.setScore(0.99f);
        tingPractice.setTimeCostInSeconds(10L);
        tingPractice.setCreatedBy(currentUser.getId());
        tingPractice.setCreatedAt(Instant.now());

        tingPracticeRepository.save(tingPractice);

        return tingPractice;
    }

    protected User createUser(boolean verified, String password) {
        User user = new User();
        user.setName(UUID.randomUUID().toString().substring(0, 20));
        user.setEmail(Instant.now().toEpochMilli() + "@example.com");
        user.setEncryptedPassword(passwordService.encrypt(password));
        user.setVerified(verified);
        user.setCreatedAt(Instant.now());

        userRepository.save(user);

        return user;
    }

    protected UserRegisterRequest createUserRegisterRequest() {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setName(UUID.randomUUID().toString().substring(0, 20));
        userRegisterRequest.setEmail(Instant.now().toEpochMilli() + "@example.com");
        userRegisterRequest.setPassword("password");
        userRegisterRequest.setConfirmPassword("password");

        return userRegisterRequest;
    }
}
