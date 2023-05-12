package com.example.Pick_Read_Me.Controller;


import com.example.Pick_Read_Me.Service.OauthService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class GitHubOAuthController {

    @Autowired
    private OauthService oauthService;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.github.redirect-uri}")
    private String redirectUri;

    @GetMapping(value = "code")
    public ResponseEntity<String> getToken(@RequestParam("code") String code) {
        return oauthService.getUserInfo(oauthService.getToken(code));
    }

    @GetMapping("/user")
    public ResponseEntity<String> getUserInfo() {
        return oauthService.getUserInfo();
    }
}
