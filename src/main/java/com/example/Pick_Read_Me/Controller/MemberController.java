package com.example.Pick_Read_Me.Controller;



import com.example.Pick_Read_Me.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository userRepository;

    @GetMapping("/member")
    public String p(){
        return "1";
    }
    @GetMapping("/oauth2/callback/github")
    public String a() {
        return "1";
    }

    @GetMapping("/home")
    public String homePage(@RequestParam("token") String token) {
        // token 값을 이용하여 필요한 처리를 수행합니다.
        System.out.println("Received token: " + token);

        // 처리 결과에 따라 적절한 응답을 반환하거나 다른 페이지로 리다이렉트할 수 있습니다.
        return "home";
    }
}