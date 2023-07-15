package com.example.Pick_Read_Me.Controller;



import com.example.Pick_Read_Me.Domain.Dto.OAuthDto.GetMemberDto;
import com.example.Pick_Read_Me.Repository.MemberRepository;
import com.example.Pick_Read_Me.Service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Log4j2
@RestController
@RequiredArgsConstructor

public class MemberController {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @GetMapping("/frontend")
    public String frontend() {
        return "헉!";
    }

    @GetMapping("/api/home") //리다이렉트 필수
    public void home(HttpServletRequest request, HttpServletResponse response,
                     @RequestParam("accessToken") String accessToken,
                     @RequestParam("refreshToken") String refreshToken) throws IOException {

        String encodedAccessToken = URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
        String encodedRefreshToken = URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);
        String redirectUrl = "http://localhost:3000/redirect?accessToken=" + encodedAccessToken + "&refreshToken=" + encodedRefreshToken;
        log.info(redirectUrl+"여기로 전송됨");
        // accessToken, refreshToken 등 필요한 작업 수행

        response.sendRedirect(redirectUrl);
    }



    @Operation(summary = "해당 유저 조회", description = "AccessToken을 헤더로 주면 회원정보를 조회합니다.")
    @GetMapping("/api/get/members")
    public ResponseEntity<GetMemberDto> getMembers(Authentication authentication) {
        log.info(authentication.getName());
        return memberService.getMembers(authentication);
    }

    @Operation(summary = "로그아웃", description = "Refresh 쿠키를 null로 만듭니다. \n따로 보낼 것은 없습니다.")
    @DeleteMapping("/api/logout")
    public void removeCookie(HttpServletResponse response) {
        Cookie myCookie = new Cookie("refreshToken", null);
        myCookie.setMaxAge(0); // 쿠키의 expiration 타임을 0으로 하여 없앤다.
        myCookie.setPath("/"); // 모든 경로에서 삭제 됬음을 알린다.
        response.addCookie(myCookie);
    }

    @Operation(summary = "Refresh Cookie를 통해 accessToken 재발급", description = "RefreshToken을 넘기면 새로운 accessToken을 발급해줍니다.")
    @GetMapping("/api/get/accessToken")
    public HttpServletResponse getAccessToken(HttpServletRequest request,
                                              HttpServletResponse response) throws IOException {
        return memberService.getAccessToken(request, response);
    }

}