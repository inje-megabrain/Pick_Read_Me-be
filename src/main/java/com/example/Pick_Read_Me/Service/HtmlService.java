package com.example.Pick_Read_Me.Service;

import io.jsonwebtoken.io.IOException;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;

@Service
public class HtmlService {
    public BufferedImage HtmlToImage(String htmlContent) {
        try {
            // HTML을 BufferedImage로 변환
            BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // HTML을 그래픽 컨텍스트에 그립니다.
            // 실제로는 Flying Saucer, Thymeleaf, Jsoup 등의 라이브러리를 사용하여 HTML을 렌더링하는 로직을 추가해야 합니다.
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, 800, 600);
            graphics.setColor(Color.BLACK);
            graphics.drawString(htmlContent, 20, 40);

            return image;
        } catch (Exception e) {
            // 변환 중 에러 처리
            e.printStackTrace();
            return null;
        }
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
}
