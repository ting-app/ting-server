package dekiru.ting.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * The root config of the Ting app,
 * notice that some configs are extracted as standalone class for convenience.
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "ting")
public class TingConfig {
    private int passwordStrength;

    private Duration confirmRegistrationExpiryDuration;

    private String confirmRegistrationReturnUrl;

    private String allowedOrigin;

    public int getPasswordStrength() {
        return passwordStrength;
    }

    public void setPasswordStrength(int passwordStrength) {
        this.passwordStrength = passwordStrength;
    }

    public Duration getConfirmRegistrationExpiryDuration() {
        return confirmRegistrationExpiryDuration;
    }

    public void setConfirmRegistrationExpiryDuration(Duration confirmRegistrationExpiryDuration) {
        this.confirmRegistrationExpiryDuration = confirmRegistrationExpiryDuration;
    }

    public String getConfirmRegistrationReturnUrl() {
        return confirmRegistrationReturnUrl;
    }

    public void setConfirmRegistrationReturnUrl(String confirmRegistrationReturnUrl) {
        this.confirmRegistrationReturnUrl = confirmRegistrationReturnUrl;
    }

    public String getAllowedOrigin() {
        return allowedOrigin;
    }

    public void setAllowedOrigin(String allowedOrigin) {
        this.allowedOrigin = allowedOrigin;
    }
}
