package com.example.Pick_Read_Me.Controller;

import com.example.Pick_Read_Me.Domain.Dto.PostDto.GetPostDto;
import com.example.Pick_Read_Me.Domain.Dto.PostDto.PostsDTO;
import com.example.Pick_Read_Me.Domain.Dto.PostDto.SelectAllPost;
import com.example.Pick_Read_Me.Domain.Entity.Post;
import com.example.Pick_Read_Me.Service.PostService;
import com.example.Pick_Read_Me.Util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.batik.transcoder.TranscoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@Api(tags="글 관련 API")
@RequestMapping("/api")
@Slf4j
public class PostController {

    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private PostService postService;

    @Operation(summary = "원하는 Repo의 ReadMe파일을 가져올 수 있는 API",
            description = "Token, 원하는 Repo이름: name 파라미터 필요\n"+"반환값은 makrdown입니다.")
    @GetMapping("/get/readmes")
    public String getReadme(Authentication authentication,
                            @RequestParam("name") String name) {


        String MarkDown= postService.getReadMe(authentication, name);
        String html = commonUtil.markdown(MarkDown);

       // return postService.extractImageUrlsFromHtml(html, name);
        return html;
    }
    @Operation(summary = "원하는 Repo의 ReadMe파일을 가져올 수 있는 API",
            description = "Token, 원하는 Repo이름: name 파라미터 필요\n"+"반환값은 makrdown입니다.")
    @GetMapping
    public List<GetPostDto> getMyPosts(Authentication authentication) {
        return postService.searchByMyPost(authentication);

    }
    @PostMapping(value = "/post/posts", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "글을 작성하는 API", description = "repo : 레포이름 등등 을 던지면 글 생성")
    public ResponseEntity<Post> createPost(Authentication authentication,
                                           @ModelAttribute PostsDTO postsDTO,
                                           @RequestParam("file") MultipartFile file) throws IOException, TranscoderException {

        // Post 작성 서비스 호출
        return postService.createPost(authentication, postsDTO, file);
    }



    @Operation(summary = "사용자의 전체 글을 조회하는 API")
    @GetMapping("/get/all/posts")
    public List<SelectAllPost> selectAllPost() {

        List<SelectAllPost> selectAllPosts = postService.selectAllPost();
        return selectAllPosts;
    }


    @Operation(summary = "사용자의 글 한 개를 조회하는 API")
    @GetMapping("/get/posts")
    public ResponseEntity<GetPostDto> selectPost(Long post_id) {

        return postService.selectPost(post_id);
    }


    @Operation(summary = "글을 삭제하는 API")
    @DeleteMapping("/delete/posts")
    public boolean deletePost(Authentication authentication, Long post_id) {

        boolean deletePost = postService.deletePost(authentication, post_id);
        return deletePost;
    }

    @Operation(summary = "게시글 좋아요 API", description = "\n 자기가 좋아요 한 글이면 true반환" +
            "\n좋아요를 달 글 파람으로 받음 -> post_id")
    @PostMapping("/like/posts")
    public ResponseEntity<Void> postLikes(Authentication authentication,
                                          @RequestParam Long post_id) {
        postService.postLikes(authentication, post_id);
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
    public Slice<GetPostDto> getPosts(Authentication authentication,
                                      @RequestParam int page) {
        return postService.searchByPost(PageRequest.ofSize(page));
    }

    @Operation(summary = "게시글 목록에서 선택하면 상세보기하는 API", description = "게시글 ID를 주면 조회할 수 있습니다")
    @GetMapping("/get/detail/post")
    public GetPostDto getDetailPost(Authentication authentication,
                              @RequestParam Long post_id) {
        return postService.getDetailPost(authentication, post_id);
    }


}
