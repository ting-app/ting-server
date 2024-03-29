package dekiru.ting.controller;

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
import dekiru.ting.Constant;
import dekiru.ting.annotation.LoginRequired;
import dekiru.ting.annotation.Me;
import dekiru.ting.config.TingConfig;
import dekiru.ting.dto.ChangePasswordRequest;
import dekiru.ting.dto.ResponseError;
import dekiru.ting.dto.UserDto;
import dekiru.ting.dto.UserRegisterRequest;
import dekiru.ting.dto.VerifyEmailRequest;
import dekiru.ting.entity.User;
import dekiru.ting.repository.UserRepository;
import dekiru.ting.service.PasswordService;
import dekiru.ting.service.RegistrationService;

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
    private RegistrationService registrationService;

    @Resource
    private RedisTemplate<String, Long> redisTemplate;

    @Autowired
    private PasswordService passwordService;

    /**
     * Create a new user.
     *
     * @param userRegisterRequest The request entity to create a new user
     * @return Created new user {@link UserDto}
     */
    @PostMapping("/users")
    public ResponseEntity<?> createUser(
            @Valid @RequestBody UserRegisterRequest userRegisterRequest) {
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

        String encryptedPassword = passwordService.encrypt(userRegisterRequest.getPassword());

        User newUser = new User();
        newUser.setName(userRegisterRequest.getName());
        newUser.setEmail(userRegisterRequest.getEmail());
        newUser.setEncryptedPassword(encryptedPassword);
        newUser.setVerified(false);
        newUser.setCreatedAt(Instant.now());

        // TODO: in the rare case, two users may register with the same name at the same time
        userRepository.save(newUser);
        registrationService.sendRegistrationConfirmEmail(newUser);

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
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest, @Me UserDto me) {
        if (!Objects.equals(changePasswordRequest.getNewPassword(),
                changePasswordRequest.getConfirmNewPassword())) {
            return new ResponseEntity<>(new ResponseError("新密码和确认密码不一致"), HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findById(me.getId()).orElse(null);

        if (!passwordService.matches(changePasswordRequest.getOldPassword(),
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
    @PostMapping("/users/confirmRegistration")
    public ResponseEntity<?> confirmRegistration(@RequestParam String key) {
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

        if (user.getVerified()) {
            return new ResponseEntity<>(
                    new ResponseError("USER_IS_ALREADY_VERIFIED", "注册已确认"),
                    HttpStatus.BAD_REQUEST);
        }

        user.setVerified(true);

        userRepository.save(user);
        redisTemplate.delete(registerKey);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Send verify email to newly registered user.
     *
     * @param verifyEmailRequest The entity to verify user's email
     * @return {@link java.lang.Void}
     */
    @PostMapping("/users/verifyEmail")
    public ResponseEntity<?> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest verifyEmailRequest) {
        User user = userRepository.findByNameOrEmail(
                verifyEmailRequest.getNameOrEmail(), verifyEmailRequest.getNameOrEmail());

        if (user == null) {
            return new ResponseEntity<>(
                    new ResponseError("USER_DOES_NOT_EXIST", "用户不存在"), HttpStatus.NOT_FOUND);
        }

        if (!passwordService.matches(verifyEmailRequest.getPassword(),
                user.getEncryptedPassword())) {
            return new ResponseEntity<>(new ResponseError("密码不正确"), HttpStatus.BAD_REQUEST);
        }

        if (user.getVerified()) {
            return new ResponseEntity<>(
                    new ResponseError("USER_IS_ALREADY_VERIFIED", "注册已确认"),
                    HttpStatus.BAD_REQUEST);
        }

        // TODO: if someone keeps sending the request, the redis memory will be exhausted
        registrationService.sendRegistrationConfirmEmail(user);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
