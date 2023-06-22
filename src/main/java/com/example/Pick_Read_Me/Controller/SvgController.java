package com.example.Pick_Read_Me.Controller;

import com.example.Pick_Read_Me.Service.SvgService;
import io.jsonwebtoken.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api")
public class SvgController {

    @Autowired
    private SvgService svgService;

    @PostMapping(value = "/convertToPNG", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> convertToPNG(@RequestParam("file") MultipartFile file) throws IOException, java.io.IOException {
        return svgService.convertToPNG(file);
    }

}
