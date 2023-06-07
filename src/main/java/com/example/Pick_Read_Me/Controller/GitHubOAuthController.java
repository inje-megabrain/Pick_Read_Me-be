package com.example.Pick_Read_Me.Controller;


import com.example.Pick_Read_Me.Service.CustomOAuth2UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/auth")
@Slf4j
public class GitHubOAuthController {


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
    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @GetMapping(value = "code")
    public void getToken(@RequestParam("code") String code) throws Exception {
        log.info("여기서는 CustomOauth2UserService가 요청을 가로챕니다");
    }
}


