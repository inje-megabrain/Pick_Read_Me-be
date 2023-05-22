package com.example.Pick_Read_Me.Controller;

import com.example.Pick_Read_Me.Jwt.JwtProvider;
import com.example.Pick_Read_Me.Service.PostService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Api(tags="Post관련 api")
@RequestMapping("/api")
public class PostController {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private PostService postService;

    @Operation(summary = "원하는 Repo의 ReadMe파일을 가져올 수 있는 API",
            description = "Token, 원하는 Repo이름: name 파라미터 필요\n"+"반환값은 makrdown입니다.")
    @GetMapping("get/readme")
    public String getReadme(HttpServletRequest request, @RequestParam("name") String name) {
        String token = request.getHeader("accessToken");
        Long github_id = Long.valueOf(jwtProvider.getGithubIdFromToken(token));
        return postService.getReadMe(github_id, name);
    }
}
