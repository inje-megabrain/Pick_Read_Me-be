package com.example.Pick_Read_Me.Controller;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class MarkdownToImageController {
    @Autowired
    private S3Client s3Client;

    @Value("${aws.credentials.bucket-name}")
    private String bucket;

    @Value("${baseurl}")
    private String baseUrl ;

    @PostMapping("/markdownToImage")
    public String markdownToImage(@RequestBody String html, @RequestParam String repoName) {
        // 이미지 URL 추출
        return extractImageUrlsFromHtml(html, repoName);
    }

    private String extractImageUrlsFromHtml(String html, String repoName) {
        List<String> imageUrls = new ArrayList<>();

        Document document = Jsoup.parse(html);


        int k=0;
        int end=1;
        int check=0;
        int copyLength=html.length();
        String copy = html;

        for(k=0; k<copyLength; k+=end) {

            int cnt = copy.indexOf("<img");
            log.info(String.valueOf(cnt));
            if(cnt!=-1) {
                for(int j=cnt+15; j<copy.length(); j++) {
                    if(copy.charAt(j)=='>') {
                        log.info("!!");
                        end = j;
                        break;
                    }

                }
                String plus = copy.substring(cnt+10, end);
                imageUrls.add(plus);
                copy = copy.substring(end, copy.length());
            }

        }




        Elements imageElements = document.select("img");
        log.info(String.valueOf(imageElements));
        for(int i=0; i<imageElements.size(); i++) {

            Element imageElement = imageElements.get(i);
            String imageUrl = imageElement.attr("src");


            String imagePath = imageUrl;
            String fileName = repoName + i;

            try {
                URL url = new URL(imagePath);
                InputStream inputStream = url.openStream();

                Path tempFilePath = Files.createTempFile("temp", ".svg"); //임시 파일 생성
                Files.copy(inputStream, tempFilePath, StandardCopyOption.REPLACE_EXISTING); //input내용을 tempFilePath에 복사

                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(fileName)
                        .build();

                String newImageUrl =  baseUrl+fileName+'"';

                html = html.replace(imageUrls.get(i), newImageUrl);

                PutObjectResponse response = s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromFile(tempFilePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return html;
    }
}
