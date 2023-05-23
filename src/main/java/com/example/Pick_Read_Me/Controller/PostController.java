package com.example.Pick_Read_Me.Controller;

import com.example.Pick_Read_Me.Domain.Dto.PostDto.PostsDTO;
import com.example.Pick_Read_Me.Domain.Entity.Post;
import com.example.Pick_Read_Me.Jwt.JwtProvider;
import com.example.Pick_Read_Me.Service.PostService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@Api(tags="글 관련 API")
@RequestMapping("/api")
public class PostController {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private PostService postService;

    @Operation(summary = "원하는 Repo의 ReadMe파일을 가져올 수 있는 API",
            description = "Token, 원하는 Repo이름: name 파라미터 필요\n"+"반환값은 makrdown입니다.")
    @GetMapping("/get/readmes")
    public String getReadme(HttpServletRequest request, @RequestParam("name") String name) {
        String token = request.getHeader("accessToken");
        Long github_id = Long.valueOf(jwtProvider.getGithubIdFromToken(token));
        return postService.getReadMe(github_id, name);
    }

    @PostMapping("/posts")
    @Operation(summary = "글을 작성하는 API",
            description = "repo : 레포이름등등 을 던지면 글 생성")
    public ResponseEntity<Post> createPost(HttpServletRequest request,
                                           @RequestBody PostsDTO postsDTO) {
        String token = request.getHeader("accessToken");
        Long github_id = Long.valueOf(jwtProvider.getGithubIdFromToken(token));
        // Post 작성 서비스 호출
        Post createdPost = postService.createPost(github_id, postsDTO);

        // 작성된 Post 객체를 HTTP 응답으로 반환
        return ResponseEntity.ok(createdPost);
    }
}
