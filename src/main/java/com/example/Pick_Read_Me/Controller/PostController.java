package com.example.Pick_Read_Me.Controller;

import com.example.Pick_Read_Me.Domain.Dto.PostDto.GetPostDto;
import com.example.Pick_Read_Me.Domain.Dto.PostDto.PostsDTO;
import com.example.Pick_Read_Me.Domain.Entity.Post;
import com.example.Pick_Read_Me.Jwt.JwtProvider;
import com.example.Pick_Read_Me.Service.PostService;
import com.example.Pick_Read_Me.Util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Api(tags="글 관련 API")
@RequestMapping("/api")
@Slf4j
public class PostController {

    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private PostService postService;

    @Operation(summary = "원하는 Repo의 ReadMe파일을 가져올 수 있는 API",
            description = "Token, 원하는 Repo이름: name 파라미터 필요\n"+"반환값은 makrdown입니다.")
    @GetMapping("/get/readmes")
    public String getReadme(HttpServletRequest request,
                            @RequestParam("name") String name, Model model) {
        String token = request.getHeader("accessToken");
        Long github_id = Long.valueOf(jwtProvider.getGithubIdFromToken(token));

        String MarkDown= postService.getReadMe(request, name);
        String html = commonUtil.markdown(MarkDown);
        return html;
    }

    @PostMapping("/posts")
    @Operation(summary = "글을 작성하는 API",
            description = "repo : 레포이름등등 을 던지면 글 생성")
    public ResponseEntity<Post> createPost(HttpServletRequest request,
                                           @RequestBody PostsDTO postsDTO) {
        String token = request.getHeader("accessToken");
        Long github_id = Long.valueOf(jwtProvider.getGithubIdFromToken(token));
        // Post 작성 서비스 호출
        Post createdPost = postService.createPost(request, postsDTO);

        // 작성된 Post 객체를 HTTP 응답으로 반환
        return ResponseEntity.ok(createdPost);
    }

    @Operation(summary = "사용자의 전체 글을 조회하는 API")
    @GetMapping("/get/all/posts")
    public List<Post> selectAllPost(HttpServletRequest request) {
        String token = request.getHeader("accessToken");
        Long github_id = Long.valueOf(jwtProvider.getGithubIdFromToken(token));

        List<Post> selectAllPost = postService.selectAllPost(request);
        return selectAllPost;
    }


    @Operation(summary = "사용자의 글 한 개를 조회하는 API")
    @GetMapping("/get/posts")
    public ResponseEntity<GetPostDto> selectPost(Long post_id) {

        return postService.selectPost(post_id);
    }


    @Operation(summary = "글을 삭제하는 API")
    @DeleteMapping("/delete/posts")
    public boolean deletePost(HttpServletRequest request, Long post_id) {

        boolean deletePost = postService.deletePost(request, post_id);
        return deletePost;
    }

    @Operation(summary = "게시글 좋아요 API", description = "\n 자기가 좋아요 한 글이면 true반환" +
            "\n좋아요를 달 글 파람으로 받음 -> post_id")
    @PostMapping("/like/posts")
    public ResponseEntity<Void> postLikes(HttpServletRequest request,
                                          @RequestParam Long post_id) {
        postService.postLikes(request, post_id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시글 수정 API", description = "게시글을 골라 수정할 수 있습니다.")
    @PutMapping("/put/posts")
    public ResponseEntity<PostsDTO> updatePost(@RequestParam Long post_id,
                                               @RequestBody PostsDTO postsDTO) {
        return postService.updatePost(post_id, postsDTO);
    }

    @Operation(summary = "게시글 랜덤 무한스크롤 API", description = "게시글을 골라 수정할 수 있습니다.\n" +
            "page는 한 페이지당 얼마만큼의 게시글을 보여줄지 정할 수 있습니다")
    @GetMapping("/get/rand/posts")
    public Slice<Post> getPosts(HttpServletRequest request,
                                @RequestParam int page) {
        log.info("!@#!@#!@##@!@#!@#@!");
        return postService.searchByPost(request,  PageRequest.ofSize(page));
    }


}
