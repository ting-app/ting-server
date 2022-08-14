package ting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ting.Constant;
import ting.dto.ResponseError;
import ting.dto.UserDto;
import ting.dto.UserLoginRequest;
import ting.entity.User;
import ting.repository.UserRepository;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
public class AuthController extends BaseController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisIndexedSessionRepository sessionRepository;

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest userLoginRequest, HttpSession session) {
        User user = userRepository.findByNameOrEmail(userLoginRequest.getNameOrEmail(), userLoginRequest.getNameOrEmail());

        if (user == null) {
            return new ResponseEntity<>(new ResponseError("用户不存在"), HttpStatus.NOT_FOUND);
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        if (!bCryptPasswordEncoder.matches(userLoginRequest.getPassword(), user.getEncryptedPassword())) {
            return new ResponseEntity<>(new ResponseError("用户名或密码不正确"), HttpStatus.BAD_REQUEST);
        }

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());

        session.setAttribute(Constant.ME, userDto);

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping("/auth/signOut")
    public ResponseEntity<Void> signOut(HttpSession session) {
        UserDto user = (UserDto) session.getAttribute(Constant.ME);

        if (user != null) {
            session.invalidate();
            sessionRepository.deleteById(session.getId());
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
