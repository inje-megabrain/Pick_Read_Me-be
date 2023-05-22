package com.example.Pick_Read_Me.Controller;



import com.example.Pick_Read_Me.Repository.MemberRepository;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Log4j2
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository userRepository;



    @GetMapping("/home")
    public String home(HttpServletRequest request, HttpServletResponse response,
                       @RequestParam("accessToken") String accessToken,
                       @RequestParam("refreshToken") String refreshToken) {
        // accessToken과 refreshToken을 활용하여 필요한 작업을 수행하는 로직을 작성합니다.
        // 예를 들어, 토큰의 유효성 검사, 토큰을 활용한 인증 등을 수행할 수 있습니다.
        System.out.println("accessToken: " + accessToken);
        System.out.println("refreshToken: " + refreshToken);
        // 응답을 생성하여 반환합니다.
        return "1";
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
}