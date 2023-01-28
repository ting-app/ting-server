package ting.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ting.BaseTest;

import javax.servlet.http.Cookie;

public class ProgramControllerTest extends BaseTest {
    @Test
    public void shouldReturn401WhenGetMyPrograms() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/me/programs"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void shouldGetMyPrograms() throws Exception {
        Cookie[] cookies = login();

        mockMvc.perform(MockMvcRequestBuilders.get("/users/me/programs").cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
