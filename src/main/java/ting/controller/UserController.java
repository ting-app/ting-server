package ting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ting.Constant;
import ting.dto.ResponseError;
import ting.dto.UserCredential;
import ting.dto.UserDto;
import ting.entity.User;
import ting.repository.UserRepository;
import ting.validation.Login;
import ting.validation.Register;

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
    public ResponseEntity<?> createUser(@Validated({Register.class}) @RequestBody UserCredential userCredential, HttpSession session) {
        if (!Objects.equals(userCredential.getPassword(), userCredential.getConfirmPassword())) {
            return new ResponseEntity<>(new ResponseError("两次密码不一致"), HttpStatus.BAD_REQUEST);
        }

        User currentUser = userRepository.findUserByName(userCredential.getName());

        if (currentUser != null) {
            return new ResponseEntity<>(new ResponseError("用户名已存在"), HttpStatus.BAD_REQUEST);
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

        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public UserDto getMe(HttpSession session) {
        return (UserDto) session.getAttribute(Constant.ME);
    }

    @PostMapping("/signOut")
    public ResponseEntity<Void> signOut(HttpSession session) {
        UserDto user = (UserDto) session.getAttribute(Constant.ME);

        if (user != null) {
            session.invalidate();
            sessionRepository.deleteById(session.getId());
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated({Login.class}) @RequestBody UserCredential userCredential, HttpSession session) {
        User user = userRepository.findUserByName(userCredential.getName());

        if (user == null) {
            return new ResponseEntity<>(new ResponseError("用户名不存在"), HttpStatus.NOT_FOUND);
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        if (!bCryptPasswordEncoder.matches(userCredential.getPassword(), user.getEncryptedPassword())) {
            return new ResponseEntity<>(new ResponseError("用户名或密码不正确"), HttpStatus.BAD_REQUEST);
        }

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());

        session.setAttribute(Constant.ME, userDto);

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}
