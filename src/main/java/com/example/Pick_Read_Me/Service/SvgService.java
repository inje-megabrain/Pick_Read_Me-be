package com.example.Pick_Read_Me.Service;

import io.jsonwebtoken.io.IOException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

@Service
public class SvgService {

    @Autowired
    private S3Client s3Client;

    @Value("${aws.credentials.bucket-name}")
    private String bucketName;

    public ResponseEntity<String> convertToPNG(MultipartFile file) throws IOException, java.io.IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        File tempFile = File.createTempFile("temp", "svg");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(file.getBytes());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }

        try {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.putObject(objectRequest, RequestBody.fromFile(tempFile));

            tempFile.delete(); // 임시 파일 삭제

            return ResponseEntity.ok("1");
        } catch (S3Exception e) {
            return ResponseEntity.ok("1");
        }
    }
}
