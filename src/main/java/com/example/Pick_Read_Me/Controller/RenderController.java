package com.example.Pick_Read_Me.Controller;

/*
import io.jsonwebtoken.io.IOException;
import lombok.extern.slf4j.Slf4j;

import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.document.DocumentFormat;
import org.jodconverter.core.office.OfficeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;


/*
@RequestMapping("/api")
@Slf4j
@RestController
public class RenderController {

    @Autowired
    DocumentConverter documentConverter;

    @PostMapping("/htmlToImage")
    public String htmlToImage(@RequestBody String htmlData) throws IOException, OfficeException, java.io.IOException {
        File inputFile = File.createTempFile("input", ".html");
        Files.write(inputFile.toPath(), htmlData.getBytes());

        String outputFilePath = "src/main/resources/output.png";
        File outputFile = new File(outputFilePath);

        DocumentFormat outputFormat = DefaultDocumentFormatRegistry.PNG;

        documentConverter.convert(inputFile).to(outputFile).as(outputFormat).execute();

        // outputFile을 처리하는 로직을 추가합니다.

        return "success";
    }

}

        //BufferedImage image = htmlService.HtmlToImage(html);

    /*
        BufferedImage thumbnail = htmlService.createThumbnail(image, 600, 600);
        log.info(String.valueOf(thumbnail));
*/
        //byte[] thumbnailData = htmlService.ImageByteArray(image);


        //String fileName = "thumbnail1.jpg";
//
       // thumbnailService.saveThumbnailToS3(thumbnailData, fileName);

     //   return "";
  //  }





