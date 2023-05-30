package com.example.Pick_Read_Me.Controller;



import com.example.Pick_Read_Me.Domain.Dto.OAuthDto.GetMemberDto;
import com.example.Pick_Read_Me.Repository.MemberRepository;
import com.example.Pick_Read_Me.Service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.parameters.P;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor

public class MemberController {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;



    @GetMapping("/home")
    public void home(HttpServletRequest request, HttpServletResponse response,
                     @RequestParam("accessToken") String accessToken,
                     @RequestParam("refreshToken") String refreshToken) throws IOException {
        String encodedAccessToken = URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
        String encodedRefreshToken = URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);
        String redirectUrl = "http://localhost:3000/redirect?accessToken=" + encodedAccessToken + "&refreshToken=" + encodedRefreshToken;

        // accessToken, refreshToken 등 필요한 작업 수행

        response.sendRedirect(redirectUrl);
    }

    @Operation(summary = "test server", description = "서버가 가동중인지 테스트하는 코드입니다.\nToken필요 X")
    @GetMapping("/api/test")
    public String test() {
        return "test url입니다. 서버 정상 가동중";

    }

    @Operation(summary = "test server", description = "서버가 가동중인지 테스트하는 코드입니다\n" +
    "Token이 맞을때만 리턴합니다.")
    @GetMapping("/token/test")
    public String test1(HttpServletRequest request) {
        System.out.println(request.getHeader("accessToken"));
        System.out.println(request.getHeader("refreshToken"));
        return "test url입니다!!. 서버 정상 가동중!";
    }

    @Operation(summary = "해당 유저 조회", description = "헤더를 주면 헤더를 까서 DB에서 조회합니다")
    @GetMapping("/api/get/members")
    public ResponseEntity<GetMemberDto> getMembers(HttpServletRequest request) {
        return memberService.getMembers(request);
    }

    @GetMapping("/api/header")
    public String hea(@RequestHeader MultiValueMap<String, String> h) {
        log.info(h);
        return h.toString();
    }
}