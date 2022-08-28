package ting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import ting.config.AwsS3Config;

import java.time.Duration;

/**
 * The service that creates the presigned url of s3 objects.
 */
@Service
public class AwsS3Service {
    public static final String READ_PERMISSION = "r";

    public static final String CREATE_PERMISSION = "c";

    @Autowired
    private AwsS3Config awsS3Config;

    /**
     * Create presigned url of s3 object.
     *
     * @param permission The permission needed to generate the access token,
     *                   currently only r (read) and c (create) are supported.
     * @param fileName   The file name of s3 object
     * @return The presigned url
     */
    public String getPresignedUrl(String permission, String fileName) {
        String bucketName = awsS3Config.getBucketName();
        DefaultCredentialsProvider credentialsProvider = DefaultCredentialsProvider.create();
        Region region = Region.of(awsS3Config.getRegion());

        try (S3Presigner s3Presigner = S3Presigner.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build()) {
            if (READ_PERMISSION.equals(permission)) {
                return getPresignedGetUrl(s3Presigner, bucketName, fileName);
            } else if (CREATE_PERMISSION.equals(permission)) {
                return getPresignedPutUrl(s3Presigner, bucketName, fileName);
            } else {
                return null;
            }
        }
    }

    private String getPresignedPutUrl(S3Presigner s3Presigner, String bucketName, String keyName) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();
        PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(awsS3Config.getWriteExpiryTimeInMinutes()))
                .putObjectRequest(putObjectRequest)
                .build();
        PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(
                putObjectPresignRequest);

        return presignedPutObjectRequest.url().toString();
    }

    private String getPresignedGetUrl(S3Presigner s3Presigner, String bucketName, String keyName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();
        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(awsS3Config.getReadExpiryTimeInMinutes()))
                .getObjectRequest(getObjectRequest)
                .build();
        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(
                getObjectPresignRequest);

        return presignedGetObjectRequest.url().toString();
    }
}
