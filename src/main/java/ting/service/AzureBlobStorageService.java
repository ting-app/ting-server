package ting.service;

import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.sas.AccountSasPermission;
import com.azure.storage.common.sas.AccountSasResourceType;
import com.azure.storage.common.sas.AccountSasService;
import com.azure.storage.common.sas.AccountSasSignatureValues;
import com.azure.storage.common.sas.SasProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ting.config.AzureBlobStorageConfig;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * The service that manages the access token of azure blobs.
 */
@Service
public class AzureBlobStorageService {
    public static final String READ_PERMISSION = "r";

    public static final String CREATE_PERMISSION = "c";

    @Autowired
    private AzureBlobStorageConfig azureBlobStorageConfig;

    /**
     * Generates access token with given permission.
     *
     * @param permission The permission needed to generate the access token,
     *                   currently only r (read) and c (create) are supported.
     * @return An object that contains the url of blob container and its access token.
     */
    public AzureBlobSas generateSas(String permission) {
        var blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(azureBlobStorageConfig.getConnectionString())
                .buildClient();
        AccountSasPermission accountSasPermission = new AccountSasPermission();
        OffsetDateTime expiryTime = OffsetDateTime.now();

        if (Objects.equals(READ_PERMISSION, permission)) {
            accountSasPermission.setReadPermission(true);

            Duration duration = Duration.ofMinutes(
                    azureBlobStorageConfig.getReadExpiryTimeInMinutes());
            expiryTime = expiryTime.plus(duration);
        } else if (Objects.equals(CREATE_PERMISSION, permission)) {
            accountSasPermission.setCreatePermission(true);

            Duration duration = Duration.ofMinutes(
                    azureBlobStorageConfig.getWriteExpiryTimeInMinutes());
            expiryTime = expiryTime.plus(duration);
        }

        AccountSasResourceType accountSasResourceType = new AccountSasResourceType()
                .setObject(true);
        AccountSasService accountSasService = new AccountSasService()
                .setBlobAccess(true);
        AccountSasSignatureValues accountSasSignatureValues = new AccountSasSignatureValues(
                expiryTime, accountSasPermission, accountSasService, accountSasResourceType)
                .setProtocol(SasProtocol.HTTPS_HTTP);
        String sas = blobServiceClient.generateAccountSas(accountSasSignatureValues);

        AzureBlobSas azureBlobSas = new AzureBlobSas();
        azureBlobSas.setContainerUrl(String.format("https://%s.blob.core.windows.net/%s", blobServiceClient.getAccountName(), azureBlobStorageConfig.getContainerName()));
        azureBlobSas.setSas(sas);

        return azureBlobSas;
    }
}
