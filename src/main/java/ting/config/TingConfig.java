package ting.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * The root config of the Ting app,
 * notice that some configs are extracted as standalone class for convenience.
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "ting")
public class TingConfig {
    private int passwordStrength;

    public int getPasswordStrength() {
        return passwordStrength;
    }

    public void setPasswordStrength(int passwordStrength) {
        this.passwordStrength = passwordStrength;
    }
}
