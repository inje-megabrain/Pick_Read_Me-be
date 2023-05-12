package com.example.Pick_Read_Me.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;

@Service
public class OauthService {
    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.github.redirect-uri}")
    private String redirectUri;
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;
    public ResponseEntity<String> getToken(String code) {
        String accessTokenUrl = "https://github.com/login/oauth/access_token";

        // 액세스 토큰 요청 파라미터 설정
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);
        requestBody.add("code", code);
        requestBody.add("redirect_uri", redirectUri);

        // 액세스 토큰 요청 수행
        WebClient.RequestBodySpec request = webClientBuilder.build().post().uri(URI.create(accessTokenUrl));
        WebClient.ResponseSpec responseSpec = request
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData(requestBody))
                .retrieve();



        // 액세스 토큰 응답 처리
        ResponseEntity<String> responseEntity = responseSpec.toEntity(String.class).block();
        if (responseEntity != null && responseEntity.getStatusCode().is2xxSuccessful()) {
            String accessTokenResponse = responseEntity.getBody();
            // 추가적인 처리 로직 작성
            return ResponseEntity.ok(accessTokenResponse);
        } else {
            // 액세스 토큰 요청이 실패한 경우 처리 로직 작성
            return ResponseEntity.status(responseEntity != null ? responseEntity.getStatusCode() : HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<String> getUserInfo(OAuth2AuthorizedClient authorizedClient) {
       
        if (authorizedClient == null) {
            // 액세스 토큰이 없는 경우 처리 로직 작성
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access token not found");
        }

        String userInfoUrl = "https://api.github.com/user";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue());
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(userInfoUrl, HttpMethod.GET, requestEntity, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            // API 응답 처리 로직 작성
            return ResponseEntity.ok(responseEntity.getBody());
        } else {
            // API 요청이 실패한 경우 처리 로직 작성
            return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
        }
    }

    private OAuth2AuthorizedClient getAuthorizedClient() {
        // 사용자의 OAuth2AuthorizedClient 객체 가져오기
        // 예시로는 GitHub를 사용하므로 providerId를 "github"로 지정합니다.
        // 필요에 따라 providerId를 수정해야 합니다.
        OAuth2AuthenticationToken authentication = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return authorizedClientService.loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName());
    }
}
