package ting.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ting.dto.Error;
import ting.dto.Response;
import ting.dto.UserDto;
import ting.entity.User;
import ting.repository.UserRepository;

import java.security.SecureRandom;
import java.util.Objects;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/")
    public Response<UserDto> register(@RequestBody UserDto user) {
        if (user == null) {
            return new Response<>(new Error("user cannot be null"));
        }

        if (StringUtils.isBlank(user.getName())) {
            return new Response<>(new Error("name cannot be empty"));
        }

        if (user.getName().length() > 20) {
            return new Response<>(new Error("name cannot be greater than 20 chars"));
        }

        if (StringUtils.isBlank(user.getPassword())) {
            return new Response<>(new Error("password cannot be empty"));
        }

        if (user.getPassword().length() < 6) {
            return new Response<>(new Error("password cannot be less than 6 chars"));
        }

        if (user.getPassword().length() > 20) {
            return new Response<>(new Error("password cannot be greater than 20 chars"));
        }

        if (StringUtils.isBlank(user.getConfirmPassword())) {
            return new Response<>(new Error("confirm password cannot be empty"));
        }

        if (!Objects.equals(user.getPassword(), user.getConfirmPassword())) {
            return new Response<>(new Error("password and confirm password has to be equal"));
        }

        User currentUser = userRepository.findUserByNameExists(user.getName());

        if (currentUser != null) {
            return new Response<>(new Error("duplicate user name"));
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(10, new SecureRandom());
        String encryptedPassword = bCryptPasswordEncoder.encode(user.getPassword());

        User newUser = new User();
        newUser.setName(user.getName());
        newUser.setEncryptedPassword(encryptedPassword);

        userRepository.save(newUser);

        user.setId(newUser.getId());

        return new Response<>(user);
    }
}
