package com.example.Pick_Read_Me.Controller;



import com.example.Pick_Read_Me.Repository.MemberRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository userRepository;



    @GetMapping("/home")
    public void home(HttpServletRequest request, HttpServletResponse response,
                     @RequestParam("accessToken") String accessToken,
                     @RequestParam("refreshToken") String refreshToken) throws IOException {
        String redirectUrl = "http://localhost:3000/redirect"; // 리다이렉션할 URL

        // accessToken, refreshToken 등 필요한 작업 수행
        response.setHeader("accessToken", accessToken);
        response.addHeader("refreshToken", refreshToken);
        response.sendRedirect(redirectUrl);
    }

    @Operation(summary = "test server", description = "서버가 가동중인지 테스트하는 코드입니다.\nToken필요 X")
    @GetMapping("/test")
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
    /**/
}