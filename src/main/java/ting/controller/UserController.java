package ting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
import ting.service.RegisterService;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * The api routes for users.
 */
@RestController
public class UserController extends BaseController {
    @SuppressWarnings("checkstyle:LineLength")
    private final Pattern emailPattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TingConfig tingConfig;

    @Autowired
    private RegisterService registerService;

    @Resource
    private RedisTemplate<String, Long> redisTemplate;

    /**
     * Create a new user.
     *
     * @param userRegisterRequest The request entity to create a new user
     * @param session             {@link javax.servlet.http.HttpSession}
     * @return Created new user {@link ting.dto.UserDto}
     */
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

        User user = userRepository.findByNameOrEmail(
                userRegisterRequest.getName(), userRegisterRequest.getEmail());

        if (user != null) {
            return new ResponseEntity<>(new ResponseError("用户名或邮箱地址已存在"), HttpStatus.BAD_REQUEST);
        }

        BCryptPasswordEncoder cryptPasswordEncoder = new BCryptPasswordEncoder(
                tingConfig.getPasswordStrength(), new SecureRandom());
        String encryptedPassword = cryptPasswordEncoder.encode(userRegisterRequest.getPassword());

        User newUser = new User();
        newUser.setName(userRegisterRequest.getName());
        newUser.setEmail(userRegisterRequest.getEmail());
        newUser.setEncryptedPassword(encryptedPassword);
        newUser.setVerified(false);
        newUser.setCreatedAt(Instant.now());

        // TODO: in the rare case, two users may register with the same name at the same time
        userRepository.save(newUser);
        registerService.sendRegisterConfirmEmail(newUser);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/users/me")
    public UserDto getMe(HttpSession session) {
        return (UserDto) session.getAttribute(Constant.ME);
    }

    /**
     * Change user's password.
     *
     * @param changePasswordRequest The request entity to change user's password
     * @param me                    Current user
     * @return {@link java.lang.Void}
     */
    @PostMapping("/users/me/changePassword")
    @LoginRequired
    public ResponseEntity<?> changePassword(
            @Valid ChangePasswordRequest changePasswordRequest, @Me UserDto me) {
        if (!Objects.equals(changePasswordRequest.getNewPassword(),
                changePasswordRequest.getConfirmNewPassword())) {
            return new ResponseEntity<>(new ResponseError("新密码和确认密码不一致"), HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findById(me.getId()).orElse(null);
        BCryptPasswordEncoder cryptPasswordEncoder = new BCryptPasswordEncoder();

        if (!cryptPasswordEncoder.matches(changePasswordRequest.getOldPassword(),
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

    /**
     * Confirm registration.
     *
     * @param key The registration key
     * @return {@link java.lang.Void}
     */
    @PostMapping("/users/registerConfirm")
    public ResponseEntity<?> registerConfirm(@RequestParam String key) {
        String registerKey = String.format("ting:register:%s", key);
        Long userId = redisTemplate.opsForValue().get(registerKey);

        if (userId == null) {
            return new ResponseEntity<>(
                    new ResponseError("REGISTER_CONFIRM_LINK_EXPIRED", "注册确认链接已过期"),
                    HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return new ResponseEntity<>(
                    new ResponseError("USER_DOES_NOT_EXIST", "用户不存在"), HttpStatus.NOT_FOUND);
        }

        if (user.isVerified()) {
            return new ResponseEntity<>(
                    new ResponseError("USER_IS_ALREADY_VERIFIED", "注册已确认"),
                    HttpStatus.BAD_REQUEST);
        }

        user.setVerified(true);

        userRepository.save(user);
        redisTemplate.delete(registerKey);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
