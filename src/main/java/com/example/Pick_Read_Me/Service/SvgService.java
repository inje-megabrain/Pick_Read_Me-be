package com.example.Pick_Read_Me.Service;

import io.jsonwebtoken.io.IOException;
import org.apache.batik.transcoder.TranscoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.util.UUID;

@Service
public class SvgService {

    @Autowired
    private S3Client s3Client;

    @Value("${aws.credentials.bucket-name}")
    private String bucketName;

    public ResponseEntity<String> convertToSvg(MultipartFile file) throws IOException, TranscoderException, java.io.IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        // Blob 파일을 SVG로 변환
        String svgContent = new String(file.getBytes());
        String svgFileName = UUID.randomUUID().toString() + ".svg";
        ByteArrayInputStream svgInputStream = new ByteArrayInputStream(svgContent.getBytes());

        // SVG 파일을 S3에 저장
        byte[] svgBytes = file.getBytes();
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(svgFileName)
                .build();
        s3Client.putObject(objectRequest, RequestBody.fromBytes(svgBytes));

        return ResponseEntity.ok(svgFileName);
    }
}
