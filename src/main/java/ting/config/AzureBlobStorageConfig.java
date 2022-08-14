package ting.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "ting.azure.storage")
public class AzureBlobStorageConfig {
    private String connectionString;

    private String containerName;

    private int readExpiryTimeInMinutes;

    private int writeExpiryTimeInMinutes;

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
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
