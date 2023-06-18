package com.example.Pick_Read_Me.Config;

// ...

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.xhtmlrenderer.swing.Java2DRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/*
@RestController
@RequestMapping("/api")
public class RenderController {

    @PostMapping("/htmlToImage")
    public String htmlToImage(@RequestBody String htmlData) {
        try {
            // Create a temporary HTML file

            Path tempFile = Path.of("src/main/resources/templates/index.html");
            File input = tempFile.toFile();

            // Write the HTML data to the temporary file
            Files.write(tempFile, htmlData.getBytes(), StandardOpenOption.WRITE);

            // Create a Java2DRenderer instance
            Java2DRenderer renderer = new Java2DRenderer(
                    input.toURI().toURL().toString(), 800, 600);

            // Render the HTML content as a BufferedImage
            BufferedImage image = renderer.getImage();

            // Save the rendered image to a PNG file in src/main/resources
            String outputPath = "src/main/resources/output.png";
            File output = new File(outputPath);
            ImageIO.write(image, "png", output);

            // Delete the temporary HTML file
            Files.deleteIfExists(tempFile);

            return "success";
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert HTML to image", e);
        }
    }
}

 */
