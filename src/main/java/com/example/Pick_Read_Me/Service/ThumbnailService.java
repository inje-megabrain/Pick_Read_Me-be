package com.example.Pick_Read_Me.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.RequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.ByteArrayInputStream;

@Service
@Slf4j
public class ThumbnailService {

    @Autowired
    private S3Client s3Client;

    @Value("${aws.bucket-name}")
    private String bucket;

    public void saveThumbnailToS3(byte[] thumbnailData, String fileName) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(thumbnailData)) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .build();

            SdkBytes imageData = SdkBytes.fromByteArray(thumbnailData);
            putObjectRequest = putObjectRequest.toBuilder()
                    .contentLength((long) imageData.asByteArray().length)
                    .build();
            log.info(String.valueOf(imageData));
            PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(thumbnailData));

            System.out.println("Thumbnail uploaded to S3. ETag: " + response.eTag());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}