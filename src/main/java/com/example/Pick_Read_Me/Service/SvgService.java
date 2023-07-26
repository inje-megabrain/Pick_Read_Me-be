package com.example.Pick_Read_Me.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import io.jsonwebtoken.io.IOException;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.UUID;

@Service
public class SvgService {

    @Autowired
    private S3Client s3Client;

    @Autowired
    private AmazonS3 amazonS3;

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

    public byte[] makeThumbnail(String name) {
        try {
            S3Object s3Object = amazonS3.getObject(bucketName, name);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            BufferedImage image = ImageIO.read(inputStream);
            BufferedImage thumbnail = Thumbnails.of(image)
                    .size(100, 100)
                    .asBufferedImage();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(thumbnail, "svg", baos);
            baos.flush();
            byte[] thumbnailBytes = baos.toByteArray();
            baos.close();

            // 썸네일을 S3에 업로드
            uploadThumbnailToS3(thumbnailBytes, "thumbnails/" + name); // 원하는 키로 설정

            return thumbnailBytes;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void uploadThumbnailToS3(byte[] thumbnailBytes, String svgFileName) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(svgFileName)
                .build();
        s3Client.putObject(objectRequest, RequestBody.fromBytes(thumbnailBytes));
    }
}
