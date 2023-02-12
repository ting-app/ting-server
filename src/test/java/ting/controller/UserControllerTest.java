package ting.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ting.BaseTest;
import ting.dto.ChangePasswordRequest;
import ting.dto.UserDto;
import ting.dto.UserLoginRequest;
import ting.dto.UserRegisterRequest;
import ting.dto.VerifyEmailRequest;
import ting.entity.User;
import ting.repository.UserRepository;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import java.util.UUID;

public class UserControllerTest extends BaseTest {
    @Autowired
    private UserRepository userRepository;

    @Resource
    private RedisTemplate<String, Long> redisTemplate;

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

    @Test
    public void shouldReturn400WhenConfirmRegistrationAndConfirmLinkNotFound() throws Exception {
        String key = UUID.randomUUID().toString();

        mockMvc.perform(MockMvcRequestBuilders.post("/users/confirmRegistration?key=" + key))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void shouldReturn404WhenConfirmRegistrationAndUserNotFound() throws Exception {
        String key = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("ting:register:" + key, 999L);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/confirmRegistration?key=" + key))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldReturn400WhenConfirmRegistrationAndUserVerified() throws Exception {
        String key = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("ting:register:" + key, currentUser.getId());

        mockMvc.perform(MockMvcRequestBuilders.post("/users/confirmRegistration?key=" + key))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void shouldConfirmRegistration() throws Exception {
        User user = createUser(false, "password");
        String key = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("ting:register:" + key, user.getId());

        mockMvc.perform(MockMvcRequestBuilders.post("/users/confirmRegistration?key=" + key))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Assertions.assertTrue(userRepository.findById(user.getId()).orElse(null).getVerified());
        Assertions.assertNull(redisTemplate.opsForValue().get("ting:register:" + key));
    }

    @Test
    public void shouldReturn400WhenVerifyEmailAndPostBodyIsInvalid() throws Exception {
        User user = createUser(true, "password");
        VerifyEmailRequest verifyEmailRequest1 = new VerifyEmailRequest();
        VerifyEmailRequest verifyEmailRequest2 = new VerifyEmailRequest();
        verifyEmailRequest2.setNameOrEmail("name");
        VerifyEmailRequest verifyEmailRequest3 = new VerifyEmailRequest();
        verifyEmailRequest3.setNameOrEmail(user.getName());
        verifyEmailRequest3.setPassword("password");

        mockMvc.perform(MockMvcRequestBuilders.post("/users/verifyEmail").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(verifyEmailRequest1)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/users/verifyEmail").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(verifyEmailRequest2)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mockMvc.perform(MockMvcRequestBuilders.post("/users/verifyEmail").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(verifyEmailRequest3)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void shouldReturn404WhenVerifyEmailAndUserNotFound() throws Exception {
        VerifyEmailRequest verifyEmailRequest = new VerifyEmailRequest();
        verifyEmailRequest.setNameOrEmail(UUID.randomUUID().toString());
        verifyEmailRequest.setPassword("password");

        mockMvc.perform(MockMvcRequestBuilders.post("/users/verifyEmail").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(verifyEmailRequest)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
