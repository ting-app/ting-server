package ting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ting.dto.ResponseError;
import ting.service.AzureBlobStorageService;
import ting.service.AzureBlobSas;

import java.util.Objects;

import static ting.service.AzureBlobStorageService.READ_PERMISSION;
import static ting.service.AzureBlobStorageService.CREATE_PERMISSION;

@RestController
public class AzureBlobStorageController extends BaseController {
    @Autowired
    private AzureBlobStorageService azureBlobStorageService;

    @GetMapping("/azureBlobs/sas")
    public ResponseEntity<?> sas(@RequestParam String permission) {
        // Currently only supports read/write permission
        if (!Objects.equals(READ_PERMISSION, permission)
                && !Objects.equals(CREATE_PERMISSION, permission)) {
            return new ResponseEntity<>(new ResponseError("不支持的权限类型"), HttpStatus.BAD_REQUEST);
        }

        AzureBlobSas azureBlobSas = azureBlobStorageService.generateSas(permission);

        return new ResponseEntity<>(azureBlobSas, HttpStatus.OK);
    }
}
