package com.example.Pick_Read_Me.Service;



import com.example.Pick_Read_Me.Domain.Entity.Member;
import com.example.Pick_Read_Me.Domain.Entity.Refresh;
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
import org.springframework.web.reactive.function.client.WebClient;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");

        // 액세스 토큰 요청 파라미터 설정
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);
        requestBody.add("code", code);

        RestTemplate restTemplate = new RestTemplate();
        // 액세스 토큰 요청
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> responseEntity = restTemplate.exchange(accessTokenUrl, HttpMethod.POST, requestEntity, Map.class);
        Map<String, String> responseBody = responseEntity.getBody();

        // 액세스 토큰 추출
        return responseBody.get("access_token");
    }


    public ResponseEntity<String> getUserInfo(String accessToken) throws Exception {
        String userInfoUrl = "https://api.github.com/user";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        // 사용자 정보 요청
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(userInfoUrl, HttpMethod.GET, requestEntity, String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
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


