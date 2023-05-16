package com.example.Pick_Read_Me.Service;

import com.example.Pick_Read_Me.Domain.Member;
import com.example.Pick_Read_Me.Domain.Refresh;
import com.example.Pick_Read_Me.Jwt.JwtProvider;
import com.example.Pick_Read_Me.Repository.MemberRepository;
import com.example.Pick_Read_Me.Repository.RefreshRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

@Service
@Slf4j
public class OauthService {
    @Autowired
    private HttpServletRequest request;
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
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private RefreshRepository refreshRepository;

    public String getToken(String code) {
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
            String accessToken = parseAccessToken(accessTokenResponse);
            // 추가적인 처리 로직 작성
            return accessToken;
        } else {
            // 액세스 토큰 요청이 실패한 경우 처리 로직 작성
            return String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String parseAccessToken(String accessTokenResponse) {
        Gson gson = new Gson();
        JsonObject responseJson = gson.fromJson(accessTokenResponse, JsonObject.class);
        String accessToken = responseJson.get("access_token").getAsString();    //access_token 값만 가져오기
        return accessToken;
    }

    public ResponseEntity<String> getUserInfo(String accessToken) throws Exception {
        if (accessToken == null || accessToken.isEmpty()) {
            // 액세스 토큰이 없는 경우 처리 로직 작성
            throw new Exception("Access token not found");
        }
        else {
            String userInfoUrl = "https://api.github.com/user";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.exchange(userInfoUrl, HttpMethod.GET, requestEntity, String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                // API 응답 처리 로직 작성
                //return ResponseEntity.ok(responseEntity.getBody());
                Gson gson = new Gson();
                JsonObject responseJson = gson.fromJson(responseEntity.getBody(), JsonObject.class);
                if (checkMember(memberRepository.findById(responseJson.get("id").getAsLong()))) {
                    HashMap<String, String> h = new HashMap<>();
                    h = saveMember(responseJson);

                    HttpHeaders header = new HttpHeaders();
                    headers.add("accessToken", h.get("accessToken"));
                    headers.add("refreshToken", h.get("refreshToken"));
                    String responseData = "Response data";

                    return ResponseEntity.ok()
                            .headers(headers)
                            .body(responseData);
                }
                else{
                    HashMap<String, String> h = new HashMap<>();
                    h = sendToken(responseJson);

                    HttpHeaders header = new HttpHeaders();
                    headers.add("accessToken", h.get("accessToken"));
                    headers.add("refreshToken", h.get("refreshToken"));
                    String responseData = "Response data";

                    return ResponseEntity.ok()
                            .headers(headers)
                            .body(responseData);
                }
            } else {
                // API 요청이 실패한 경우 처리 로직 작성
                throw new Exception("API 요청 실패");
            }
        }
    }

    private HashMap<String, String> sendToken(JsonObject responseJson) {
        HashMap<String, String> m = new HashMap<>();

        m.put("githubId", String.valueOf(responseJson.get("id")));
        String accessToken, refreshToken;
        accessToken = jwtProvider.generateToken(m);
        refreshToken = jwtProvider.generateRefreshToken(m);
        Member member = new Member();
        Refresh refresh = new Refresh();
        member.setId(responseJson.get("id").getAsLong());

        Refresh r = refreshRepository.findById(member.getId()).orElseGet(Refresh::new);
        member.setRefresh(refresh);

        refresh.setIp(getIP());
        refresh.setRefreshToken(refreshToken);
        refresh.setMember(member);
        refreshRepository.save(r);
        memberRepository.save(member);


        HashMap<String, String> send = new HashMap<>();
        send.put("accessToken", accessToken);
        send.put("refreshToken", refreshToken);
        return send;
    }

    private boolean checkMember(Optional<Member> id) {
        if(id.isPresent())
            return false;
        else return true;
    }

    public HashMap<String, String> saveMember(JsonObject responseJson) {
        Member member = new Member();
        Refresh refresh = new Refresh();

        member.setCreated(new Date());
        member.setName(String.valueOf(responseJson.get("name")));
        member.setEmail(responseJson.get("email").getAsString());
        member.setId(responseJson.get("id").getAsLong());
        member.setRoles(Collections.singletonList("User"));

        HashMap<String, String> m = new HashMap<>();

        String accessToken, refreshToken;
        accessToken = jwtProvider.generateToken(m);
        refreshToken = jwtProvider.generateRefreshToken(m);
        member.setRefresh(refresh);

        refresh.setIp(getIP());
        refresh.setRefreshToken(refreshToken);

        refresh.setMember(member);
        refreshRepository.save(refresh);
        memberRepository.save(member);


        HashMap<String, String> send = new HashMap<>();
        send.put("accessToken", accessToken);
        send.put("refreshToken", refreshToken);
        return send;
    }
    public String getIP() {
        String ipAddress = request.getRemoteAddr();
        return ipAddress;
    }
}
