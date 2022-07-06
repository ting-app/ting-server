package ting.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ting.Constant;
import ting.dto.Error;
import ting.dto.NewUserDto;
import ting.dto.Response;
import ting.dto.UserDto;
import ting.entity.User;
import ting.repository.UserRepository;

import javax.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Objects;

@RestController
@RequestMapping("/users")
public class UserController extends BaseController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisIndexedSessionRepository sessionRepository;

    @PostMapping
    public Response<UserDto> register(@RequestBody NewUserDto newUser, HttpSession session) {
        if (newUser == null) {
            return new Response<>(new Error("user cannot be null"));
        }

        if (StringUtils.isBlank(newUser.getName())) {
            return new Response<>(new Error("name cannot be empty"));
        }

        if (newUser.getName().length() > 20) {
            return new Response<>(new Error("name cannot be greater than 20 chars"));
        }

        if (StringUtils.isBlank(newUser.getPassword())) {
            return new Response<>(new Error("password cannot be empty"));
        }

        if (newUser.getPassword().length() < 6) {
            return new Response<>(new Error("password cannot be less than 6 chars"));
        }

        if (newUser.getPassword().length() > 20) {
            return new Response<>(new Error("password cannot be greater than 20 chars"));
        }

        if (StringUtils.isBlank(newUser.getConfirmPassword())) {
            return new Response<>(new Error("confirm password cannot be empty"));
        }

        if (!Objects.equals(newUser.getPassword(), newUser.getConfirmPassword())) {
            return new Response<>(new Error("password and confirm password has to be equal"));
        }

        User currentUser = userRepository.findUserByName(newUser.getName());

        if (currentUser != null) {
            return new Response<>(new Error("duplicate user name"));
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(10, new SecureRandom());
        String encryptedPassword = bCryptPasswordEncoder.encode(newUser.getPassword());

        User user = new User();
        user.setName(newUser.getName());
        user.setEncryptedPassword(encryptedPassword);

        userRepository.save(user);

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());

        session.setAttribute(Constant.ME, userDto);

        return new Response<>(userDto);
    }

    @GetMapping("/me")
    public Response<UserDto> me(HttpSession session) {
        return new Response<>((UserDto) session.getAttribute(Constant.ME));
    }

    @PostMapping("/signOut")
    public Response<Void> signOut(HttpSession session) {
        UserDto user = (UserDto) session.getAttribute(Constant.ME);

        if (user != null) {
            session.invalidate();
            sessionRepository.deleteById(session.getId());
        }

        return new Response<>(null);
    }
}
