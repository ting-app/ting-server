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

@Service
public class AzureBlobStorageService {
    public final static String READ_PERMISSION = "r";

    public final static String CREATE_PERMISSION = "c";

    @Autowired
    private AzureBlobStorageConfig azureBlobStorageConfig;

    public BlobSas generateSas(String permission) {
        var blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(azureBlobStorageConfig.getConnectionString())
                .buildClient();
        AccountSasPermission accountSasPermission = new AccountSasPermission();
        OffsetDateTime expiryTime = OffsetDateTime.now();

        if (Objects.equals(READ_PERMISSION, permission)) {
            accountSasPermission.setReadPermission(true);
            expiryTime = expiryTime.plus(Duration.ofMinutes(azureBlobStorageConfig.getReadExpiryTimeInMinutes()));
        } else if (Objects.equals(CREATE_PERMISSION, permission)) {
            accountSasPermission.setCreatePermission(true);
            expiryTime = expiryTime.plus(Duration.ofMinutes(azureBlobStorageConfig.getWriteExpiryTimeInMinutes()));
        }

        AccountSasResourceType accountSasResourceType = new AccountSasResourceType()
                .setObject(true);
        AccountSasService accountSasService = new AccountSasService()
                .setBlobAccess(true);
        AccountSasSignatureValues accountSasSignatureValues = new AccountSasSignatureValues(expiryTime, accountSasPermission, accountSasService, accountSasResourceType)
                .setProtocol(SasProtocol.HTTPS_HTTP);
        String sas = blobServiceClient.generateAccountSas(accountSasSignatureValues);

        BlobSas blobSas = new BlobSas();
        blobSas.setContainerUrl(String.format("https://%s.blob.core.windows.net/%s", blobServiceClient.getAccountName(), azureBlobStorageConfig.getContainerName()));
        blobSas.setSas(sas);

        return blobSas;
    }
}
