package com.example.Pick_Read_Me.Service;

import com.example.Pick_Read_Me.Domain.Dto.OAuthDto.OauthMemberDto;
import com.example.Pick_Read_Me.Domain.Dto.OAuthDto.Token;
import com.example.Pick_Read_Me.Domain.Entity.Member;
import com.example.Pick_Read_Me.Domain.Entity.Refresh;
import com.example.Pick_Read_Me.Jwt.JwtProvider;
import com.example.Pick_Read_Me.Repository.MemberRepository;
import com.example.Pick_Read_Me.Repository.RefreshRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtProvider tokenService;
    private final MemberRepository userRequestMapper;
    private final ObjectMapper objectMapper;

    /*
    
     */
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RefreshRepository refreshRepository;
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();

        OauthMemberDto userDto = new OauthMemberDto();
        userDto.setId((Long) oAuth2User.getAttributes().get("id"));
        userDto.setName((String) oAuth2User.getAttributes().get("name"));
        userDto.setEmail((String) oAuth2User.getAttributes().get("email"));
        userDto.setRepo((String) oAuth2User.getAttributes().get("repo"));
        userDto.setCreated(new Date());

        log.info("Principal에서 꺼낸 OAuth2User = {}", oAuth2User);
        // 최초 로그인이라면 회원가입 처리를 한다.
        Optional<Member> optionalMember = memberRepository.findById(userDto.getId());
        Refresh checkIp = refreshRepository.findById(userDto.getId()).orElseGet(Refresh::new);
        if (optionalMember.isPresent()) {
            Member find = optionalMember.get();
            log.info("업데이트");
            log.info("토큰 발행 시작");

            HashMap<String, String> m = new HashMap<>();
            m.put("githubId", String.valueOf(userDto.getId()));

            Token token = new Token();
            token.setAccessToken(tokenService.generateToken(m));
            token.setRefreshToken(tokenService.generateRefreshToken(m));

            String ip = request.getRemoteAddr();

            find.setName(String.valueOf(oAuth2User.getAttributes().get("name")));
            find.setEmail(String.valueOf(oAuth2User.getAttributes().get("email")));
            find.setRepo(String.valueOf(oAuth2User.getAttributes().get("repo")));
            find.setProfile(String.valueOf(oAuth2User.getAttributes().get("profile")));
            find.setUpdated(new Date());
            find.setRoles(Collections.singletonList("USER"));

            checkIp.setMember(find);
            checkIp.setRefreshToken(token.getRefreshToken());
            checkIp.setId(find.getId());

            if(!ip.equals(checkIp.getIp())) {
                checkIp.setIp(ip);
            }

            find.setRefresh(checkIp);

            memberRepository.save(find);
            refreshRepository.save(checkIp);

            log.info("{}", token);
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("/api/home")
                    .queryParam("accessToken", token.getAccessToken())
                    .queryParam("refreshToken", token.getRefreshToken());
            String redirectUrl = uriBuilder.toUriString();
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        }
        else{
                String targetUrl;
                log.info("토큰 발행 시작");

                HashMap<String, String> m = new HashMap<>();
                m.put("githubId", String.valueOf(userDto.getId()));

                Token token = new Token();
                token.setAccessToken(tokenService.generateToken(m));
                token.setRefreshToken(tokenService.generateRefreshToken(m));

                String ip = request.getRemoteAddr();

                Refresh refresh = new Refresh();

                Member member = new Member();
                member.setId((Long) oAuth2User.getAttributes().get("id"));
                member.setName(String.valueOf(oAuth2User.getAttributes().get("name")));
                member.setEmail(String.valueOf(oAuth2User.getAttributes().get("email")));
                member.setRepo(String.valueOf(oAuth2User.getAttributes().get("repo")));
                member.setProfile(String.valueOf(oAuth2User.getAttributes().get("profile")));
                member.setCreated(new Date());
                member.setUpdated(new Date());
                member.setRoles(Collections.singletonList("USER"));
                member.setPassword("");

                refresh.setMember(member);
                refresh.setRefreshToken(token.getRefreshToken());
                refresh.setIp(request.getRemoteAddr());
                refresh.setId(member.getId());
                member.setRefresh(refresh);

                memberRepository.save(member);
                refreshRepository.save(refresh);

                log.info("{}", token);
                UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("/api/home")
                        .queryParam("accessToken", token.getAccessToken())
                        .queryParam("refreshToken", token.getRefreshToken());
                String redirectUrl = uriBuilder.toUriString();
                getRedirectStrategy().sendRedirect(request, response, redirectUrl);

        }
    }
}