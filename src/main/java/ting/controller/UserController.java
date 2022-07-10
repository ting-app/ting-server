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
import ting.dto.ResponseError;
import ting.dto.Response;
import ting.dto.UserCredential;
import ting.dto.UserDto;
import ting.entity.User;
import ting.repository.UserRepository;

import javax.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Objects;

@RestController
@RequestMapping("/users")
public class UserController extends BaseController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisIndexedSessionRepository sessionRepository;

    @PostMapping
    public Response<UserDto> createUser(@RequestBody UserCredential userCredential, HttpSession session) {
        if (userCredential == null) {
            return new Response<>(new ResponseError("姓名不能为空"));
        }

        if (StringUtils.isBlank(userCredential.getName())) {
            return new Response<>(new ResponseError("姓名不能为空"));
        }

        if (userCredential.getName().length() > 20) {
            return new Response<>(new ResponseError("姓名不能超过20个字符"));
        }

        if (StringUtils.isBlank(userCredential.getPassword())) {
            return new Response<>(new ResponseError("密码不能为空"));
        }

        if (userCredential.getPassword().length() < 6) {
            return new Response<>(new ResponseError("密码不能少于6个字符"));
        }

        if (userCredential.getPassword().length() > 20) {
            return new Response<>(new ResponseError("密码不能超过20个字符"));
        }

        if (StringUtils.isBlank(userCredential.getConfirmPassword())) {
            return new Response<>(new ResponseError("确认密码不能为空"));
        }

        if (!Objects.equals(userCredential.getPassword(), userCredential.getConfirmPassword())) {
            return new Response<>(new ResponseError("两次密码不一致"));
        }

        User currentUser = userRepository.findUserByName(userCredential.getName());

        if (currentUser != null) {
            return new Response<>(new ResponseError("用户名已存在"));
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(10, new SecureRandom());
        String encryptedPassword = bCryptPasswordEncoder.encode(userCredential.getPassword());

        User newUser = new User();
        newUser.setName(userCredential.getName());
        newUser.setEncryptedPassword(encryptedPassword);
        newUser.setCreatedAt(Instant.now());

        // TODO: in the rare case, two users may register with the same name at the same time
        userRepository.save(newUser);

        UserDto userDto = new UserDto();
        userDto.setId(newUser.getId());
        userDto.setName(newUser.getName());

        session.setAttribute(Constant.ME, userDto);

        return new Response<>(userDto);
    }

    @GetMapping("/me")
    public Response<UserDto> getMe(HttpSession session) {
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
            return new Response<>(new ResponseError(message));
        }

        User user = userRepository.findUserByName(userCredential.getName());

        if (user == null) {
            return new Response<>(new ResponseError("用户名不存在"));
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        if (!bCryptPasswordEncoder.matches(userCredential.getPassword(), user.getEncryptedPassword())) {
            return new Response<>(new ResponseError(message));
        }

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());

        session.setAttribute(Constant.ME, userDto);

        return new Response<>(userDto);
    }
}
