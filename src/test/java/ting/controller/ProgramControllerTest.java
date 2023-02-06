package ting.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ting.BaseTest;
import ting.dto.ProgramDto;
import ting.entity.Program;
import ting.repository.ProgramRepository;

import javax.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ProgramControllerTest extends BaseTest {
    @Autowired
    private ProgramRepository programRepository;

    @Test
    public void shouldReturn401WhenGetMyPrograms() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/me/programs"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void shouldGetMyPrograms() throws Exception {
        createMyProgram(1, true);

        Cookie[] cookies = login();
        String body = mockMvc.perform(MockMvcRequestBuilders.get("/users/me/programs").cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<ProgramDto> programDtos = objectMapper.readValue(body, new TypeReference<>() {
        });

        Assertions.assertTrue(programDtos.size() > 0);

        // Programs returned should be created by current user
        for (ProgramDto programDto : programDtos) {
            Assertions.assertEquals(programDto.getCreatedBy(), currentUser.getId());
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
        createMyProgram(1, false);
        createMyProgram(1, true);
        createMyProgram(2, true);

        String body = mockMvc.perform(MockMvcRequestBuilders.get("/programs?page=1&pageSize=10&language=1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
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
        Program program = createMyProgram(1, false);
        createMyProgram(1, true);
        createMyProgram(2, true);

        Cookie[] cookies = login();
        String body = mockMvc.perform(MockMvcRequestBuilders.get("/programs?page=1&pageSize=10&language=1").cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<ProgramDto> programDtos = objectMapper.readValue(body, new TypeReference<>() {
        });

        Assertions.assertTrue(programDtos.size() > 0);

        for (ProgramDto programDto : programDtos) {
            Assertions.assertEquals(1, programDto.getLanguage());
        }

        ProgramDto programDto = programDtos.stream()
                .filter(it -> it.getCreatedBy().equals(currentUser.getId()))
                .filter(it -> !it.getVisible())
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(programDto);
        Assertions.assertEquals(program.getId(), programDto.getId());
    }

    @Test
    public void shouldReturn401WhenCreateProgram() throws Exception {
        ProgramDto programDto = createProgramDto(1, true);
        String json = objectMapper.writeValueAsString(programDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/programs").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void shouldCreateProgram() throws Exception {
        ProgramDto programDto = createProgramDto(1, true);
        String json = objectMapper.writeValueAsString(programDto);

        Cookie[] cookies = login();
        String body = mockMvc.perform(MockMvcRequestBuilders.post("/programs").contentType(MediaType.APPLICATION_JSON).content(json).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ProgramDto newProgramDto = objectMapper.readValue(body, ProgramDto.class);

        Assertions.assertEquals(currentUser.getId(), newProgramDto.getCreatedBy());
    }

    @Test
    public void shouldReturn404WhenProgramNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/programs/999"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldReturnForbiddenWhenGetInvisibleProgram() throws Exception {
        Program program = createOtherUserProgram(1, false);

        Cookie[] cookies = login();
        mockMvc.perform(MockMvcRequestBuilders.get("/programs/" + program.getId()).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
        mockMvc.perform(MockMvcRequestBuilders.get("/programs/" + program.getId()))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void shouldGetProgramById() throws Exception {
        Program program = createMyProgram(1, true);

        String body = mockMvc.perform(MockMvcRequestBuilders.get("/programs/" + program.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ProgramDto programDto = objectMapper.readValue(body, ProgramDto.class);

        Assertions.assertEquals(programDto.getId(), program.getId());
    }

    @Test
    public void shouldReturn401WhenDeleteProgram() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/programs/999"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void shouldReturn404WhenDeleteProgram() throws Exception {
        Cookie[] cookies = login();

        mockMvc.perform(MockMvcRequestBuilders.delete("/programs/999").cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldReturn401WhenDeleteOtherUsersProgram() throws Exception {
        Program program = createOtherUserProgram(1, true);
        Cookie[] cookies = login();

        mockMvc.perform(MockMvcRequestBuilders.delete("/programs/" + program.getId()).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void shouldDeleteProgram() throws Exception {
        Program program = createMyProgram(1, true);
        Cookie[] cookies = login();

        mockMvc.perform(MockMvcRequestBuilders.delete("/programs/" + program.getId()).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Assertions.assertNull(programRepository.findById(program.getId()).orElse(null));
    }
}
