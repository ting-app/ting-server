package ting.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ting.BaseTest;
import ting.dto.ChangePasswordRequest;
import ting.dto.UserDto;
import ting.dto.UserLoginRequest;
import ting.dto.UserRegisterRequest;
import ting.entity.User;

import javax.servlet.http.Cookie;

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
        User user = createUser(true, "password");
        UserRegisterRequest userRegisterRequest1 = createUserRegisterRequest();
        userRegisterRequest1.setName(user.getName());
        UserRegisterRequest userRegisterRequest2 = createUserRegisterRequest();
        userRegisterRequest2.setEmail(user.getEmail());

        mockMvc.perform(MockMvcRequestBuilders.post("/users").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userRegisterRequest1)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/users").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userRegisterRequest2)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void shouldGetCurrentUser() throws Exception {
        Cookie[] cookies = login();

        String body = mockMvc.perform(MockMvcRequestBuilders.get("/users/me").cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        UserDto userDto = objectMapper.readValue(body, UserDto.class);

        Assertions.assertNotNull(userDto);
        Assertions.assertEquals(currentUser.getId(), userDto.getId());
    }

    @Test
    public void shouldReturn401WhenChangePasswordAndCurrentUserIsNotLoggedIn() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword("old");
        changePasswordRequest.setNewPassword("123456");
        changePasswordRequest.setConfirmNewPassword("123456");

        mockMvc.perform(MockMvcRequestBuilders.post("/users/me/changePassword").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void shouldReturn400WhenChangePasswordAndPostBodyIsInvalid() throws Exception {
        ChangePasswordRequest changePasswordRequest1 = new ChangePasswordRequest();
        ChangePasswordRequest changePasswordRequest2 = new ChangePasswordRequest();
        changePasswordRequest2.setOldPassword("old");
        ChangePasswordRequest changePasswordRequest3 = new ChangePasswordRequest();
        changePasswordRequest3.setOldPassword("old");
        changePasswordRequest3.setNewPassword("new");
        ChangePasswordRequest changePasswordRequest4 = new ChangePasswordRequest();
        changePasswordRequest4.setOldPassword("old");
        changePasswordRequest4.setNewPassword("123456");
        changePasswordRequest4.setConfirmNewPassword("123457");
        ChangePasswordRequest changePasswordRequest5 = new ChangePasswordRequest();
        changePasswordRequest5.setOldPassword("old");
        changePasswordRequest5.setNewPassword("123456");
        changePasswordRequest5.setConfirmNewPassword("123456");

        Cookie[] cookies = login();

        mockMvc.perform(MockMvcRequestBuilders.post("/users/me/changePassword").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest1)).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/users/me/changePassword").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest2)).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/users/me/changePassword").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest3)).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/users/me/changePassword").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest4)).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/users/me/changePassword").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest5)).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void shouldChangePassword() throws Exception {
        User user = createUser(true, "password");
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword("password");
        changePasswordRequest.setNewPassword("password2");
        changePasswordRequest.setConfirmNewPassword("password2");
        Cookie[] cookies = login(user, "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/users/me/changePassword").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordRequest)).cookie(cookies))
                .andExpect(MockMvcResultMatchers.status().isOk());

        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setNameOrEmail(user.getName());
        userLoginRequest.setPassword("password2");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userLoginRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
