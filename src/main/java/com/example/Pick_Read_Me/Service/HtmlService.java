package com.example.Pick_Read_Me.Service;


import io.jsonwebtoken.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
@Service
@Slf4j
public class HtmlService {

    /*
    public BufferedImage HtmlToImage(String htmlContent) {
        try {
            // Parse the HTML content using Jsoup
            Document doc = Jsoup.parse(htmlContent);

            // Create a BufferedImage object with the desired width and height
            int width = 800;
            int height = 600;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            // Get the graphics context of the image
            Graphics2D graphics = image.createGraphics();

            // Set the background color
            graphics.setColor(Color.BLACK);
            graphics.fillRect(0, 0, width, height);

            // Render the HTML content onto the image
            Element body = doc.body();
            renderElement(body, graphics);
            String imagePath = "src/main/resources/image.jpg"; // 저장할 이미지 파일 경로

            File output = new File(imagePath);
            ImageIO.write(image, "jpg", output); // 이미지 파일로 저장
            return image;
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception or return null
            return null;
        }
    }

    private void renderElement(Element element, Graphics2D graphics) throws IOException, java.io.IOException {
        String tagName = element.tagName();

        if ("img".equalsIgnoreCase(tagName)) {
            // If the element is an <img> tag, load the image and draw it on the graphics context
            String src = element.attr("src");
            BufferedImage img = loadImage(src);
            if (img != null) {
                int x = Integer.parseInt(element.attr("x"));
                int y = Integer.parseInt(element.attr("y"));
                graphics.drawImage(img, x, y, null);

                System.out.println("Image drawn: " + src + ", x: " + x + ", y: " + y);
            }
        } else {
            // If the element is not an <img> tag, recursively render its children
            for (Element child : element.children()) {
                renderElement(child, graphics);
            }
        }
    }

    private BufferedImage loadImage(String src) throws IOException, java.io.IOException {
        // Load the image from the specified URL
        URL url = new URL(src);
        log.info(String.valueOf(url));
        return ImageIO.read(url);
    }



    public BufferedImage createThumbnail(BufferedImage image, int width, int height) {
        // 썸네일 생성 로직 구현
        BufferedImage thumbnail = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = thumbnail.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(image, 0, 0, width, height, null);
        graphics.dispose();
        return thumbnail;
    }


    public byte[] ImageByteArray(BufferedImage image) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

     */
}