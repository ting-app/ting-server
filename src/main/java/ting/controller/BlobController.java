package ting.controller;

import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.sas.AccountSasPermission;
import com.azure.storage.common.sas.AccountSasResourceType;
import com.azure.storage.common.sas.AccountSasService;
import com.azure.storage.common.sas.AccountSasSignatureValues;
import com.azure.storage.common.sas.SasProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ting.config.AzureBlobStorageConfig;
import ting.dto.BlobSas;
import ting.dto.ResponseError;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Objects;

@RestController
public class BlobController extends BaseController {
    private final static String READ_PERMISSION = "r";

    private final static String WRITE_PERMISSION = "w";

    @Autowired
    private AzureBlobStorageConfig azureBlobStorageConfig;

    @GetMapping("/blobs/sas")
    public ResponseEntity<?> sas(@RequestParam String permission) {
        // Currently only supports read/write permission
        if (!Objects.equals(READ_PERMISSION, permission) && !Objects.equals(WRITE_PERMISSION, permission)) {
            return new ResponseEntity<>(new ResponseError("不支持的权限类型"), HttpStatus.BAD_REQUEST);
        }

        var blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(azureBlobStorageConfig.getConnectionString())
                .buildClient();
        AccountSasPermission accountSasPermission = new AccountSasPermission();
        OffsetDateTime expiryTime = OffsetDateTime.now();

        if (Objects.equals(READ_PERMISSION, permission)) {
            accountSasPermission.setReadPermission(true);
            expiryTime = expiryTime.plus(Duration.ofMinutes(azureBlobStorageConfig.getReadExpiryTimeInMinutes()));
        } else if (Objects.equals(WRITE_PERMISSION, permission)) {
            accountSasPermission.setWritePermission(true);
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

        return new ResponseEntity<>(blobSas, HttpStatus.OK);
    }
}
