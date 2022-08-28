package ting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ting.dto.ResponseError;
import ting.service.AwsS3Service;

import java.util.Objects;

import static ting.service.AwsS3Service.CREATE_PERMISSION;
import static ting.service.AwsS3Service.READ_PERMISSION;

/**
 * The api routes for amazon s3.
 */
@RestController
public class AwsS3Controller extends BaseController {
    @Autowired
    private AwsS3Service awsS3Service;

    /**
     * Create presigned url of s3 object.
     *
     * @param permission The permission needed to generate the access token,
     *                   currently only r (read) and c (create) are supported.
     * @param fileName   The file name of s3 object
     * @return The presigned url
     */
    @GetMapping("/s3/presignedUrl")
    public ResponseEntity<?> getPresignedUrl(String permission, String fileName) {
        // Currently only supports read/write permission
        if (!Objects.equals(READ_PERMISSION, permission)
                && !Objects.equals(CREATE_PERMISSION, permission)) {
            return new ResponseEntity<>(new ResponseError("不支持的权限类型"), HttpStatus.BAD_REQUEST);
        }

        String url = awsS3Service.getPresignedUrl(permission, fileName);

        return new ResponseEntity<>(url, HttpStatus.OK);
    }
}
