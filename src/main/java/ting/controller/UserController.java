package ting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ting.Constant;
import ting.annotation.LoginRequired;
import ting.annotation.Me;
import ting.config.TingConfig;
import ting.dto.ChangePasswordRequest;
import ting.dto.ResponseError;
import ting.dto.UserDto;
import ting.dto.UserRegisterRequest;
import ting.entity.User;
import ting.repository.UserRepository;
import ting.service.AwsSesService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;

@RestController
public class UserController extends BaseController {
    private final Pattern emailPattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TingConfig tingConfig;

    @Autowired
    private AwsSesService awsSesService;

    @PostMapping("/users")
    public ResponseEntity<?> createUser(
            @Valid @RequestBody UserRegisterRequest userRegisterRequest, HttpSession session) {
        if (!Objects.equals(userRegisterRequest.getPassword(),
                userRegisterRequest.getConfirmPassword())) {
            return new ResponseEntity<>(new ResponseError("两次密码不一致"), HttpStatus.BAD_REQUEST);
        }

        if (!emailPattern.matcher(userRegisterRequest.getEmail()).matches()) {
            return new ResponseEntity<>(new ResponseError("邮箱地址不合法"), HttpStatus.BAD_REQUEST);
        }

        User userByName = userRepository.findByName(userRegisterRequest.getName());
        User userByEmail = userRepository.findByEmail(userRegisterRequest.getEmail());

        if (userByName != null || userByEmail != null) {
            return new ResponseEntity<>(new ResponseError("用户名或邮箱地址已存在"), HttpStatus.BAD_REQUEST);
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(
                tingConfig.getPasswordStrength(), new SecureRandom());
        String encryptedPassword = bCryptPasswordEncoder.encode(userRegisterRequest.getPassword());

        User newUser = new User();
        newUser.setName(userRegisterRequest.getName());
        newUser.setEmail(userRegisterRequest.getEmail());
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

    @GetMapping("/users/me")
    public UserDto getMe(HttpSession session) {
        return (UserDto) session.getAttribute(Constant.ME);
    }

    @PostMapping("/users/me/changePassword")
    @LoginRequired
    public ResponseEntity<?> changePassword(
            @Valid ChangePasswordRequest changePasswordRequest, @Me UserDto me) {
        if (!Objects.equals(changePasswordRequest.getNewPassword(),
                changePasswordRequest.getConfirmNewPassword())) {
            return new ResponseEntity<>(new ResponseError("新密码和确认密码不一致"), HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByName(me.getName());
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        if (!bCryptPasswordEncoder.matches(changePasswordRequest.getOldPassword(),
                user.getEncryptedPassword())) {
            return new ResponseEntity<>(new ResponseError("旧密码不正确"), HttpStatus.BAD_REQUEST);
        }

        String newEncryptedPassword = new BCryptPasswordEncoder(
                tingConfig.getPasswordStrength(), new SecureRandom())
                .encode(changePasswordRequest.getNewPassword());
        user.setEncryptedPassword(newEncryptedPassword);

        userRepository.save(user);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
