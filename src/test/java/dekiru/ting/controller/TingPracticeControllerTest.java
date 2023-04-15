package dekiru.ting.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import dekiru.ting.BaseTest;
import dekiru.ting.dto.TingPracticeDto;
import dekiru.ting.entity.Program;
import dekiru.ting.entity.Ting;

import javax.servlet.http.Cookie;
import java.util.List;

public class TingPracticeControllerTest extends BaseTest {
    @Test
    public void shouldReturn401WhenCreateTingPracticeAndUserIsNotLoggedIn() throws Exception {
        TingPracticeDto tingPracticeDto = createTingPracticeDto(999);
        String json = objectMapper.writeValueAsString(tingPracticeDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/tingPractices").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void shouldReturn404WhenCreateTingPracticeAndTingNotFound() throws Exception {
        TingPracticeDto tingPracticeDto = createTingPracticeDto(999);
        String json = objectMapper.writeValueAsString(tingPracticeDto);
        Cookie[] cookies = login();

        mockMvc.perform(MockMvcRequestBuilders.post("/tingPractices").contentType(MediaType.APPLICATION_JSON).content(json).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldReturn400WhenCreateTingPracticeAndPostBodyIsNotValid() throws Exception {
        TingPracticeDto tingPracticeDto1 = new TingPracticeDto();
        TingPracticeDto tingPracticeDto2 = new TingPracticeDto();
        tingPracticeDto2.setTingId(1L);
        TingPracticeDto tingPracticeDto3 = new TingPracticeDto();
        tingPracticeDto3.setTingId(1L);
        tingPracticeDto3.setContent("content");
        TingPracticeDto tingPracticeDto4 = new TingPracticeDto();
        tingPracticeDto4.setTingId(1L);
        tingPracticeDto4.setContent("content");
        tingPracticeDto4.setScore(0.98f);

        Cookie[] cookies = login();

        mockMvc.perform(MockMvcRequestBuilders.post("/tingPractices").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(tingPracticeDto1)).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/tingPractices").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(tingPracticeDto2)).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/tingPractices").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(tingPracticeDto3)).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/tingPractices").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(tingPracticeDto4)).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void shouldCreateTingPractice() throws Exception {
        Program program = createMyProgram(1, true);
        Ting ting = createTing(program.getId());
        TingPracticeDto tingPracticeDto = createTingPracticeDto(ting.getId());

        Cookie[] cookies = login();

        String body = mockMvc.perform(MockMvcRequestBuilders.post("/tingPractices").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(tingPracticeDto)).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        TingPracticeDto newTingPracticeDto = objectMapper.readValue(body, TingPracticeDto.class);

        Assertions.assertNotNull(newTingPracticeDto);
        Assertions.assertEquals(ting.getId(), newTingPracticeDto.getTingId());
    }

    @Test
    public void shouldReturn400WhenGetTingPracticesAndParametersAreInvalid() throws Exception {
        Cookie[] cookies = login();

        mockMvc.perform(MockMvcRequestBuilders.get("/tingPractices").cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.get("/tingPractices?createdBy=1").cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.get("/tingPractices?createdBy=1&page=1").cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.get("/tingPractices?createdBy=1&pageSize=10").cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.get("/tingPractices?createdBy=1&page=-1&pageSize=10").cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.get("/tingPractices?createdBy=1&page=1&pageSize=-10").cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.get("/tingPractices?createdBy=1&page=1&pageSize=999").cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void shouldGetTingPractices() throws Exception {
        Program program = createMyProgram(1, true);
        Ting ting = createTing(program.getId());
        createMyTingPractice(ting.getId());
        createMyTingPractice(ting.getId());
        createMyTingPractice(ting.getId());

        Cookie[] cookies = login();
        String body = mockMvc.perform(MockMvcRequestBuilders.get("/tingPractices?createdBy=" + currentUser.getId() + "&page=1&pageSize=2").cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<TingPracticeDto> tingPracticeDtos = objectMapper.readValue(body, new TypeReference<>() {
        });

        Assertions.assertNotNull(tingPracticeDtos);
        Assertions.assertEquals(2, tingPracticeDtos.size());

        for (TingPracticeDto tingPracticeDto : tingPracticeDtos) {
            Assertions.assertEquals(currentUser.getId(), tingPracticeDto.getCreatedBy());
            Assertions.assertNotNull(tingPracticeDto.getTingTitle());
        }
    }

    @Test
    public void shouldGetTingPracticesAndSetTingTitleToDeletedWhenTingDeleted() throws Exception {
        createMyTingPractice(999);
        createMyTingPractice(999);

        Cookie[] cookies = login();
        String body = mockMvc.perform(MockMvcRequestBuilders.get("/tingPractices?createdBy=" + currentUser.getId() + "&page=1&pageSize=2").cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<TingPracticeDto> tingPracticeDtos = objectMapper.readValue(body, new TypeReference<>() {
        });

        Assertions.assertNotNull(tingPracticeDtos);

        for (TingPracticeDto tingPracticeDto : tingPracticeDtos) {
            Assertions.assertEquals("听力已删除", tingPracticeDto.getTingTitle());
        }
    }
}
