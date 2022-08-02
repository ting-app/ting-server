package ting.controller;

import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.sas.AccountSasPermission;
import com.azure.storage.common.sas.AccountSasResourceType;
import com.azure.storage.common.sas.AccountSasService;
import com.azure.storage.common.sas.AccountSasSignatureValues;
import com.azure.storage.common.sas.SasProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ting.annotation.LoginRequired;
import ting.config.AzureBlobStorageConfig;

import java.time.Duration;
import java.time.OffsetDateTime;

@RestController
public class BlobController extends BaseController {
    @Autowired
    private AzureBlobStorageConfig azureBlobStorageConfig;

    @GetMapping("/blobs/sas")
    @LoginRequired
    public String sas() {
        var blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(azureBlobStorageConfig.getConnectionString())
                .buildClient();
        AccountSasPermission accountSasPermission = new AccountSasPermission()
                .setWritePermission(true);
        AccountSasResourceType accountSasResourceType = new AccountSasResourceType()
                .setObject(true);
        AccountSasService accountSasService = new AccountSasService()
                .setBlobAccess(true);
        OffsetDateTime expiryTime = OffsetDateTime.now().plus(Duration.ofMinutes(5));
        AccountSasSignatureValues accountSasSignatureValues = new AccountSasSignatureValues(expiryTime, accountSasPermission, accountSasService, accountSasResourceType)
                .setProtocol(SasProtocol.HTTPS_HTTP);
        String sas = blobServiceClient.generateAccountSas(accountSasSignatureValues);

        return String.format("https://%s.blob.core.windows.net/%s?%s", blobServiceClient.getAccountName(), azureBlobStorageConfig.getContainerName(), sas);
    }
}
