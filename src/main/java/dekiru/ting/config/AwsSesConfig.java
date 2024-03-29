package dekiru.ting.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Config for Amazon Simple Email Service.
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "ting.aws.ses")
public class AwsSesConfig {
    private String region;

    private String fromAddress;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }
}
