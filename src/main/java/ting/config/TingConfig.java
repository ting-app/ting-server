package ting.config;

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

    private Duration registerConfirmExpiryDuration;

    private String registerConfirmReturnUrl;

    private String allowedOrigin;

    public int getPasswordStrength() {
        return passwordStrength;
    }

    public void setPasswordStrength(int passwordStrength) {
        this.passwordStrength = passwordStrength;
    }

    public Duration getRegisterConfirmExpiryDuration() {
        return registerConfirmExpiryDuration;
    }

    public void setRegisterConfirmExpiryDuration(Duration registerConfirmExpiryDuration) {
        this.registerConfirmExpiryDuration = registerConfirmExpiryDuration;
    }

    public String getRegisterConfirmReturnUrl() {
        return registerConfirmReturnUrl;
    }

    public void setRegisterConfirmReturnUrl(String registerConfirmReturnUrl) {
        this.registerConfirmReturnUrl = registerConfirmReturnUrl;
    }

    public String getAllowedOrigin() {
        return allowedOrigin;
    }

    public void setAllowedOrigin(String allowedOrigin) {
        this.allowedOrigin = allowedOrigin;
    }
}
