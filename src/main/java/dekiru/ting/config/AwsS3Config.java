package dekiru.ting.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Config for Amazon S3.
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "ting.aws.s3")
public class AwsS3Config {
    private String region;

    private String bucketName;

    private int readExpiryTimeInMinutes;

    private int writeExpiryTimeInMinutes;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public int getReadExpiryTimeInMinutes() {
        return readExpiryTimeInMinutes;
    }

    public void setReadExpiryTimeInMinutes(int readExpiryTimeInMinutes) {
        this.readExpiryTimeInMinutes = readExpiryTimeInMinutes;
    }

    public int getWriteExpiryTimeInMinutes() {
        return writeExpiryTimeInMinutes;
    }

    public void setWriteExpiryTimeInMinutes(int writeExpiryTimeInMinutes) {
        this.writeExpiryTimeInMinutes = writeExpiryTimeInMinutes;
    }
}
