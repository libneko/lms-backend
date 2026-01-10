package com.neko.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;

@Data
@AllArgsConstructor
@Slf4j
public class AWSS3Util {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    public String uploadUserImage(byte[] bytes, String objectName) {
        return upload(bytes, objectName, "user_img/");
    }

    public String uploadBookImage(byte[] bytes, String objectName) {
        return upload(bytes, objectName, "book_img/");
    }

    public String upload(byte[] bytes, String objectName, String prefix) {
        String key = prefix + objectName;
        try (S3Client s3Client = S3Client.builder()
                .endpointOverride(URI.create("https://s3." + endpoint))
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, accessKeySecret)))
                .region(Region.AWS_GLOBAL)
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(false)
                        .chunkedEncodingEnabled(false)
                        .build())
                .build()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
            String fileUrl = String.format("https://%s.%s/%s", bucketName, endpoint, key);
            log.info("File upload success: {}", fileUrl);
            return fileUrl;
        } catch (Exception e) {
            log.error("File upload failed: {}", e.getMessage());
            return null;
        }
    }
}