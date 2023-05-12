package com.example.Pick_Read_Me.Controller;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {


    @GetMapping("/")
    public String t (){
        return " 1";
    }
}