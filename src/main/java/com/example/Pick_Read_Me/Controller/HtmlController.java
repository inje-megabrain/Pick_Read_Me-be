package com.example.Pick_Read_Me.Controller;

import com.example.Pick_Read_Me.Service.HtmlService;
import com.example.Pick_Read_Me.Service.ThumbnailService;
import io.jsonwebtoken.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

@RestController
@RequestMapping("/api")
@Slf4j
public class HtmlController {

    @Autowired
    private HtmlService htmlService;


    @Autowired
    private ThumbnailService thumbnailService;
    @GetMapping("/html/thumbnail")
    public String HtmlToThumbnail(String html) throws Exception {
        BufferedImage image = htmlService.HtmlToImage(html);

    /*
        BufferedImage thumbnail = htmlService.createThumbnail(image, 600, 600);
        log.info(String.valueOf(thumbnail));
*/
        byte[] thumbnailData = htmlService.ImageByteArray(image);


        String fileName = "thumbnail1.jpg";

        thumbnailService.saveThumbnailToS3(thumbnailData, fileName);

        return "";
    }



}
