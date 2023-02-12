package ting.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ting.BaseTest;
import ting.dto.UserRegisterRequest;
import ting.entity.User;

public class UserControllerTest extends BaseTest {
    @Test
    public void shouldReturn400WhenCreateUserAndPostBodyIsInvalid() throws Exception {
        UserRegisterRequest userRegisterRequest1 = new UserRegisterRequest();
        UserRegisterRequest userRegisterRequest2 = new UserRegisterRequest();
        userRegisterRequest2.setName("name");
        UserRegisterRequest userRegisterRequest3 = new UserRegisterRequest();
        userRegisterRequest3.setName("name");
        userRegisterRequest3.setEmail("email");
        UserRegisterRequest userRegisterRequest4 = new UserRegisterRequest();
        userRegisterRequest4.setName("name");
        userRegisterRequest4.setEmail("email");
        userRegisterRequest4.setPassword("password");
        UserRegisterRequest userRegisterRequest5 = new UserRegisterRequest();
        userRegisterRequest5.setName("name");
        userRegisterRequest5.setEmail("email");
        userRegisterRequest5.setPassword("password");
        userRegisterRequest5.setConfirmPassword("password2");
        UserRegisterRequest userRegisterRequest6 = new UserRegisterRequest();
        userRegisterRequest6.setName("name");
        userRegisterRequest6.setEmail("email");
        userRegisterRequest6.setPassword("password");
        userRegisterRequest6.setConfirmPassword("password");

        mockMvc.perform(MockMvcRequestBuilders.post("/users").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userRegisterRequest1)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/users").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userRegisterRequest2)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/users").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userRegisterRequest3)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/users").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userRegisterRequest4)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/users").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userRegisterRequest5)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/users").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userRegisterRequest6)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void shouldReturn400WhenCreateUserAndDuplicatedNameOrEmailFound() throws Exception {
        User user = createUser(true);
        UserRegisterRequest userRegisterRequest1 = createUserRegisterRequest();
        userRegisterRequest1.setName(user.getName());
        UserRegisterRequest userRegisterRequest2 = createUserRegisterRequest();
        userRegisterRequest2.setEmail(user.getEmail());

        mockMvc.perform(MockMvcRequestBuilders.post("/users").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userRegisterRequest1)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/users").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userRegisterRequest2)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
