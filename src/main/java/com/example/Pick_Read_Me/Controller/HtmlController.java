package com.example.Pick_Read_Me.Controller;

import com.example.Pick_Read_Me.Service.HtmlService;
import com.example.Pick_Read_Me.Service.ThumbnailService;
import io.jsonwebtoken.io.IOException;
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
public class HtmlController {

    @Autowired
    private HtmlService htmlService;


    @Autowired
    private ThumbnailService thumbnailService;
    @GetMapping("/html/thumbnail")
    public String HtmlToThumbnail(String html) {
        BufferedImage image = htmlService.HtmlToImage(html);

        BufferedImage thumbnail = htmlService.createThumbnail(image, 200, 200);

        byte[] thumbnailData = htmlService.ImageByteArray(thumbnail);
        String fileName = "thumbnail1.jpg";

        thumbnailService.saveThumbnailToS3(thumbnailData, fileName);

        return "";
    }



}
