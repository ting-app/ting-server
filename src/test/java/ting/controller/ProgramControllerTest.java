package ting.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ting.BaseTest;
import ting.dto.ProgramDto;
import ting.entity.Program;

import javax.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ProgramControllerTest extends BaseTest {
    @Test
    public void shouldReturn401WhenGetMyPrograms() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/me/programs"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void shouldGetMyPrograms() throws Exception {
        createProgram(1, true);

        Cookie[] cookies = login();
        String body = mockMvc.perform(MockMvcRequestBuilders.get("/users/me/programs").cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        List<ProgramDto> programDtos = objectMapper.readValue(body, new TypeReference<>() {
        });

        Assertions.assertTrue(programDtos.size() > 0);

        // Programs returned should be created by current user
        for (ProgramDto programDto : programDtos) {
            Assertions.assertEquals(programDto.getCreatedBy(), user.getId());
        }
    }

    @Test
    public void shouldReturn400WhenGetPrograms() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/programs").characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.get("/programs?page=1&pageSize=10&language=-1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("language 参数无效")));
        mockMvc.perform(MockMvcRequestBuilders.get("/programs?page=-1&pageSize=10&language=1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("page 参数无效")));
        mockMvc.perform(MockMvcRequestBuilders.get("/programs?page=1&pageSize=-10&language=1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("pageSize 参数无效")));
        mockMvc.perform(MockMvcRequestBuilders.get("/programs?page=1&pageSize=10000&language=1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("pageSize 超过最大值")));
    }

    @Test
    public void shouldGetProgramsForAnonymousUser() throws Exception {
        createProgram(1, false);
        createProgram(1, true);
        createProgram(2, true);

        String body = mockMvc.perform(MockMvcRequestBuilders.get("/programs?page=1&pageSize=10&language=1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        List<ProgramDto> programDtos = objectMapper.readValue(body, new TypeReference<>() {
        });

        Assertions.assertTrue(programDtos.size() > 0);

        for (ProgramDto programDto : programDtos) {
            Assertions.assertTrue(programDto.getVisible());
            Assertions.assertEquals(1, programDto.getLanguage());
        }
    }

    @Test
    public void shouldGetProgramsForLoginUser() throws Exception {
        Program program = createProgram(1, false);
        createProgram(1, true);
        createProgram(2, true);

        Cookie[] cookies = login();
        String body = mockMvc.perform(MockMvcRequestBuilders.get("/programs?page=1&pageSize=10&language=1").cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        List<ProgramDto> programDtos = objectMapper.readValue(body, new TypeReference<>() {
        });

        Assertions.assertTrue(programDtos.size() > 0);

        for (ProgramDto programDto : programDtos) {
            Assertions.assertEquals(1, programDto.getLanguage());
        }

        ProgramDto programDto = programDtos.stream()
                .filter(it -> it.getCreatedBy().equals(user.getId()))
                .filter(it -> !it.getVisible())
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(programDto);
        Assertions.assertEquals(program.getId(), programDto.getId());
    }
}
