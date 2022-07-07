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
import ting.dto.Response;
import ting.dto.UserCredential;
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
    public Response<UserDto> register(@RequestBody UserCredential userCredential, HttpSession session) {
        if (userCredential == null) {
            return new Response<>(new Error("user cannot be null"));
        }

        if (StringUtils.isBlank(userCredential.getName())) {
            return new Response<>(new Error("name cannot be empty"));
        }

        if (userCredential.getName().length() > 20) {
            return new Response<>(new Error("name cannot be greater than 20 chars"));
        }

        if (StringUtils.isBlank(userCredential.getPassword())) {
            return new Response<>(new Error("password cannot be empty"));
        }

        if (userCredential.getPassword().length() < 6) {
            return new Response<>(new Error("password cannot be less than 6 chars"));
        }

        if (userCredential.getPassword().length() > 20) {
            return new Response<>(new Error("password cannot be greater than 20 chars"));
        }

        if (StringUtils.isBlank(userCredential.getConfirmPassword())) {
            return new Response<>(new Error("confirm password cannot be empty"));
        }

        if (!Objects.equals(userCredential.getPassword(), userCredential.getConfirmPassword())) {
            return new Response<>(new Error("password and confirm password has to be equal"));
        }

        User currentUser = userRepository.findUserByName(userCredential.getName());

        if (currentUser != null) {
            return new Response<>(new Error("duplicate user name"));
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(10, new SecureRandom());
        String encryptedPassword = bCryptPasswordEncoder.encode(userCredential.getPassword());

        User newUser = new User();
        newUser.setName(userCredential.getName());
        newUser.setEncryptedPassword(encryptedPassword);

        // TODO: in the rare case, two users may register with the same name at the same time
        userRepository.save(newUser);

        UserDto userDto = new UserDto();
        userDto.setId(newUser.getId());
        userDto.setName(newUser.getName());

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

    @PostMapping("/login")
    public Response<UserDto> login(@RequestBody UserCredential userCredential, HttpSession session) {
        String message = "用户名或密码不正确";

        if (userCredential == null || StringUtils.isBlank(userCredential.getName())
                || StringUtils.isBlank(userCredential.getPassword())) {
            return new Response<>(new Error(message));
        }

        User user = userRepository.findUserByName(userCredential.getName());

        if (user == null) {
            return new Response<>(new Error("用户名不存在"));
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        if (!bCryptPasswordEncoder.matches(userCredential.getPassword(), user.getEncryptedPassword())) {
            return new Response<>(new Error(message));
        }

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());

        session.setAttribute(Constant.ME, userDto);

        return new Response<>(userDto);
    }
}
