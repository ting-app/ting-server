package ting.controller;

import org.junit.jupiter.api.Assertions;
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
    public void shouldReturn401WhenProgramDoesntBelongToCurrentUser() throws Exception {
        Program program = createOtherUserProgram(1, true);
        TingDto tingDto = createTingDto(program.getId());
        String json = objectMapper.writeValueAsString(tingDto);
        Cookie[] cookies = login();

        mockMvc.perform(MockMvcRequestBuilders.post("/tings").contentType(MediaType.APPLICATION_JSON).content(json).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void shouldReturn400WhenCreateTing() throws Exception {
        Program program = createMyProgram(1, true);
        TingDto tingDto1 = new TingDto();
        TingDto tingDto2 = new TingDto();
        tingDto2.setProgramId(program.getId());
        TingDto tingDto3 = new TingDto();
        tingDto3.setProgramId(program.getId());
        tingDto3.setTitle("title");
        TingDto tingDto4 = new TingDto();
        tingDto4.setProgramId(program.getId());
        tingDto4.setTitle("title");
        tingDto4.setDescription("description");
        TingDto tingDto5 = new TingDto();
        tingDto5.setProgramId(program.getId());
        tingDto5.setTitle("title");
        tingDto5.setDescription("description");
        tingDto5.setAudioUrl("audio");

        Cookie[] cookies = login();

        mockMvc.perform(MockMvcRequestBuilders.post("/tings").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(tingDto1)).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/tings").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(tingDto2)).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/tings").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(tingDto3)).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/tings").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(tingDto4)).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/tings").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(tingDto5)).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void shouldCreateTing() throws Exception {
        Program program = createMyProgram(1, true);
        TingDto tingDto = createTingDto(program.getId());

        Cookie[] cookies = login();
        String body = mockMvc.perform(MockMvcRequestBuilders.post("/tings").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(tingDto)).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        TingDto newTingDto = objectMapper.readValue(body, TingDto.class);
        
        Assertions.assertEquals(program.getId(), newTingDto.getProgramId());
    }
}
