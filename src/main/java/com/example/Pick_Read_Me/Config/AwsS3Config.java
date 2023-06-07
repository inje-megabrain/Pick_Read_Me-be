package com.example.Pick_Read_Me.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class AwsS3Config {

    @Value("${aws.access-key}")
    private String accessKey;

    @Value("${aws.secret-key}")
    private String secretKey;

    @Value("${aws.region}")
    private String region;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        String endpoint = "https://s3-" + region + ".amazonaws.com"; // 엔드포인트 문자열 생성
        return S3Client.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(endpoint)) // 버킷의 엔드포인트를 사용
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

}