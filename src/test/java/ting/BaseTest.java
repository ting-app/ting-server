package ting;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ting.dto.UserLoginRequest;
import ting.entity.Program;
import ting.entity.User;
import ting.repository.ProgramRepository;
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
    PasswordService passwordService;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private ProgramRepository programRepository;

    protected User user;

    private String password = "123";

    @BeforeEach
    public void setup() {
        user = new User();
        user.setName(UUID.randomUUID().toString());
        user.setEmail("example@example.com");
        user.setEncryptedPassword(passwordService.encrypt(password));
        user.setVerified(true);
        user.setCreatedAt(Instant.now());

        userRepository.save(user);
    }

    protected Cookie[] login() throws Exception {
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

    protected Program createProgram(int language, boolean visible) {
        Program program = new Program();
        program.setLanguage(language);
        program.setTitle(UUID.randomUUID().toString());
        program.setVisible(visible);
        program.setDescription("Description");
        program.setCreatedBy(user.getId());
        program.setCreatedAt(Instant.now());
        program.setUpdatedAt(Instant.now());

        programRepository.save(program);

        return program;
    }
}
