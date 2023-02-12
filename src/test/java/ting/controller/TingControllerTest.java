package ting.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ting.BaseTest;
import ting.dto.TingDto;
import ting.entity.Program;
import ting.entity.Ting;
import ting.repository.TingRepository;

import javax.servlet.http.Cookie;
import java.util.List;

public class TingControllerTest extends BaseTest {
    @Autowired
    private TingRepository tingRepository;

    @Test
    public void shouldReturn401WhenCreateTingAndCurrentUserIsNotLoggedIn() throws Exception {
        Program program = createMyProgram(1, true);
        TingDto tingDto = createTingDto(program.getId());
        String json = objectMapper.writeValueAsString(tingDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/tings").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void shouldReturn400WhenCreateTingAndProgramDoesntExist() throws Exception {
        TingDto tingDto = createTingDto(999);
        String json = objectMapper.writeValueAsString(tingDto);
        Cookie[] cookies = login();

        mockMvc.perform(MockMvcRequestBuilders.post("/tings").contentType(MediaType.APPLICATION_JSON).content(json).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void shouldReturn403WhenCreateTingAndProgramDoesntBelongToCurrentUser() throws Exception {
        Program program = createOtherUserProgram(1, true);
        TingDto tingDto = createTingDto(program.getId());
        String json = objectMapper.writeValueAsString(tingDto);
        Cookie[] cookies = login();

        mockMvc.perform(MockMvcRequestBuilders.post("/tings").contentType(MediaType.APPLICATION_JSON).content(json).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void shouldReturn400WhenCreateTingAndPostBodyIsInvalid() throws Exception {
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

    @Test
    public void shouldReturn401WhenDeleteTingAndCurrentUserIsNotLoggedIn() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/tings/999"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void shouldReturn404WhenDeleteTingAndTingNotFound() throws Exception {
        Cookie[] cookies = login();

        mockMvc.perform(MockMvcRequestBuilders.delete("/tings/999").cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldReturn403WhenDeleteTingAndProgramIsNotCreatedByCurrentUser() throws Exception {
        Program program = createOtherUserProgram(1, true);
        Ting ting = createTing(program.getId());

        Cookie[] cookies = login();

        mockMvc.perform(MockMvcRequestBuilders.delete("/tings/" + ting.getId()).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void shouldDeleteTing() throws Exception {
        Program program = createMyProgram(1, true);
        Ting ting = createTing(program.getId());

        Cookie[] cookies = login();

        mockMvc.perform(MockMvcRequestBuilders.delete("/tings/" + ting.getId()).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Assertions.assertNull(tingRepository.findById(ting.getId()).orElse(null));
    }

    @Test
    public void shouldReturn401WhenUpdateTingAndCurrentUserIsNotLoggedIn() throws Exception {
        TingDto tingDto = createTingDto(999);

        mockMvc.perform(MockMvcRequestBuilders.put("/tings/999").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(tingDto)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void shouldReturn400WhenUpdateTingAndPostBodyIsInvalid() throws Exception {
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

        mockMvc.perform(MockMvcRequestBuilders.put("/tings/999").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(tingDto1)).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.put("/tings/999").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(tingDto2)).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.put("/tings/999").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(tingDto3)).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.put("/tings/999").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(tingDto4)).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.put("/tings/999").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(tingDto5)).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void shouldReturn404WhenUpdateTingAndTingNotFound() throws Exception {
        TingDto tingDto = createTingDto(999);
        Cookie[] cookies = login();

        mockMvc.perform(MockMvcRequestBuilders.put("/tings/999").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(tingDto)).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldReturn403WhenUpdateTingAndProgramIsNotCreatedByCurrentUser() throws Exception {
        Program program = createOtherUserProgram(1, true);
        Ting ting = createTing(program.getId());
        TingDto tingDto = createTingDto(program.getId());
        tingDto.setId(ting.getId());

        Cookie[] cookies = login();

        mockMvc.perform(MockMvcRequestBuilders.put("/tings/" + tingDto.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(tingDto)).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void shouldReturn403WhenUpdateTingAndAssociatedProgramIsNotCorrect() throws Exception {
        Program program1 = createMyProgram(1, true);
        Program program2 = createMyProgram(1, true);
        Ting ting = createTing(program1.getId());
        TingDto tingDto = createTingDto(program2.getId());
        tingDto.setId(ting.getId());

        Cookie[] cookies = login();

        mockMvc.perform(MockMvcRequestBuilders.put("/tings/" + tingDto.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(tingDto)).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void shouldUpdateTing() throws Exception {
        Program program = createMyProgram(1, true);
        Ting ting = createTing(program.getId());
        TingDto tingDto = createTingDto(program.getId());
        tingDto.setId(ting.getId());
        tingDto.setTitle("updated");

        Cookie[] cookies = login();

        mockMvc.perform(MockMvcRequestBuilders.put("/tings/" + tingDto.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(tingDto)).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Ting newTing = tingRepository.findById(ting.getId()).orElse(null);

        Assertions.assertEquals(tingDto.getTitle(), newTing.getTitle());
    }

    @Test
    public void shouldReturn404WhenGetTingByIdAndTingNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/tings/999"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldGetTingById() throws Exception {
        Program program = createMyProgram(1, true);
        Ting ting = createTing(program.getId());

        String body = mockMvc.perform(MockMvcRequestBuilders.get("/tings/" + ting.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        TingDto tingDto = objectMapper.readValue(body, TingDto.class);

        Assertions.assertNotNull(tingDto);
        Assertions.assertEquals(tingDto.getId(), ting.getId());
    }

    @Test
    public void shouldReturn400WhenGetTingsAndParametersAreInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/tings"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.get("/tings?programId=999"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.get("/tings?programId=999&page=-1&pageSize=10"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.get("/tings?programId=999&page=1&pageSize=-10"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.get("/tings?programId=999&page=1&pageSize=999"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void shouldReturn404WhenGetTingsAndProgramNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/tings?programId=999&page=1&pageSize=10"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldReturn403WhenGetTingsAndProgramIsInvisibleAndNotCreatedByCurrentUser() throws Exception {
        Program program = createOtherUserProgram(1, false);

        Cookie[] cookies = login();

        mockMvc.perform(MockMvcRequestBuilders.get("/tings?programId=" + program.getId() + "&page=1&pageSize=10"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
        mockMvc.perform(MockMvcRequestBuilders.get("/tings?programId=" + program.getId() + "&page=1&pageSize=10").cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void shouldGetTings() throws Exception {
        Program program = createMyProgram(1, true);
        createTing(program.getId());
        createTing(program.getId());
        createTing(program.getId());

        String body = mockMvc.perform(MockMvcRequestBuilders.get("/tings?page=1&pageSize=2&programId=" + program.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<TingDto> tingDtos = objectMapper.readValue(body, new TypeReference<>() {
        });

        Assertions.assertTrue(tingDtos.size() > 0);
        Assertions.assertEquals(2, tingDtos.size());

        for (TingDto tingDto : tingDtos) {
            Assertions.assertEquals(program.getId(), tingDto.getProgramId());
        }
    }

    @Test
    public void shouldGetTingsCount() throws Exception {
        Program program = createMyProgram(1, true);
        createTing(program.getId());
        createTing(program.getId());
        createTing(program.getId());

        String body = mockMvc.perform(MockMvcRequestBuilders.get("/tings:count?programId=" + program.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        int count = objectMapper.readValue(body, Integer.class);

        Assertions.assertEquals(3, count);
    }
}
