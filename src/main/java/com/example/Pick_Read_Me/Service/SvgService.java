package com.example.Pick_Read_Me.Service;

import io.jsonwebtoken.io.IOException;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;

import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import com.aspose.imaging.Image;
import com.aspose.imaging.License;
import com.aspose.imaging.ResizeType;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;


@Service
public class SvgService {

    @Autowired
    private S3Client s3Client;


    @Value("${cloud.aws.credentials.bucketName}")
    private String bucketName;

    public ResponseEntity<String> convertToSvg(MultipartFile file, String title) throws IOException, TranscoderException, java.io.IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        // Blob 파일을 SVG로 변환
        String svgContent = new String(file.getBytes());
        String svgFileName = title + ".svg";
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

    private void uploadThumbnailToS3(byte[] thumbnailBytes, String svgFileName) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(svgFileName)
                .build();
        s3Client.putObject(objectRequest, RequestBody.fromBytes(thumbnailBytes));
    }


    public String makeThumbnail(String name) {
        try {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key("narinn.png")
                    .build();

            System.out.println("Attempting to read object: " + name);
            ResponseInputStream<GetObjectResponse> getObjectResponse = s3Client.getObject(getObjectRequest);
            byte[] objectBytes = getObjectResponse.readAllBytes();


        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }


        return "";
    }
}