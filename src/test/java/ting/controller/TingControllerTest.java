package ting.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ting.BaseTest;
import ting.dto.TingDto;
import ting.entity.Program;

import javax.servlet.http.Cookie;

public class TingControllerTest extends BaseTest {
    @Test
    public void shouldReturn401WhenCreateTing() throws Exception {
        Program program = createMyProgram(1, true);
        TingDto tingDto = createTingDto(program.getId());
        String json = objectMapper.writeValueAsString(tingDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/tings").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void shouldReturn400WhenProgramDoesntExist() throws Exception {
        TingDto tingDto = createTingDto(999);
        String json = objectMapper.writeValueAsString(tingDto);
        Cookie[] cookies = login();

        mockMvc.perform(MockMvcRequestBuilders.post("/tings").contentType(MediaType.APPLICATION_JSON).content(json).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void shouldReturn400WhenProgramDoesntBelongToCurrentUser() throws Exception {
        Program program = createOtherUserProgram(1, true);
        TingDto tingDto = createTingDto(program.getId());
        String json = objectMapper.writeValueAsString(tingDto);
        Cookie[] cookies = login();

        mockMvc.perform(MockMvcRequestBuilders.post("/tings").contentType(MediaType.APPLICATION_JSON).content(json).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
