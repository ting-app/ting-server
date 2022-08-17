package ting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ting.config.TingConfig;
import ting.entity.User;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * The service that manages user registration.
 */
@Service
public class RegisterService {
    @Autowired
    private TingConfig tingConfig;

    @Autowired
    private AwsSesService awsSesService;

    @Resource
    private RedisTemplate<String, Long> redisTemplate;

    /**
     * Send a confirmation email of registration to user.
     *
     * @param user The target user
     */
    public void sendRegistrationConfirmEmail(User user) {
        String uuid = UUID.randomUUID().toString();
        String key = String.format("ting:register:%s", uuid);

        redisTemplate.opsForValue()
                .set(key, user.getId(), tingConfig.getConfirmRegistrationExpiryDuration());
        awsSesService.send(user.getEmail(),
                "Ting 注册确认", buildRegistrationConfirmEmailContent(uuid));
    }

    private String buildRegistrationConfirmEmailContent(String uuid) {
        String url = tingConfig.getConfirmRegistrationReturnUrl() + "?key=" + uuid;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<p>欢迎注册 Ting，请点击下方链接完成注册：</p>");
        stringBuilder.append(String.format("<p><a href=\"%s\">%s</a></p>", url, url));
        stringBuilder.append("<p>本邮件由系统自动生成，请勿直接回复。</p>");

        return stringBuilder.toString();
    }
}
