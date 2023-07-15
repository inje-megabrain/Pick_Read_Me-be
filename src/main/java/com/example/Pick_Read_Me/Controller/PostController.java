package com.example.Pick_Read_Me.Controller;

import com.example.Pick_Read_Me.Domain.Dto.PostDto.GetPostDto;
import com.example.Pick_Read_Me.Domain.Dto.PostDto.PostsDTO;
import com.example.Pick_Read_Me.Domain.Dto.PostDto.SelectAllPost;
import com.example.Pick_Read_Me.Domain.Entity.Post;
import com.example.Pick_Read_Me.Repository.PostRepository;
import com.example.Pick_Read_Me.Service.PostService;
import com.example.Pick_Read_Me.Util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.batik.transcoder.TranscoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
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
    private PostRepository postRepository;

    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private PostService postService;

    @Operation(summary = "원하는 Repo의 ReadMe파일을 가져올 수 있는 API (accessToken 필수)",
            description = "예시)/get/readmes?name=sleeg00를 URL로 입력하면 sleeg00의 Markdown를 가져오고 html로 Parsing해서 반환합니다.")
    @GetMapping("/get/readmes")
    public String getReadme(Authentication authentication,
                            @RequestParam("name") String name) {


        String MarkDown= postService.getReadMe(authentication, name);
        String html = commonUtil.markdown(MarkDown);

       // return postService.extractImageUrlsFromHtml(html, name);
        return html;
    }

    @Operation(summary = "자기글 무한 스크롤(AccessToken 필수)",
            description = "자기글을 모두 조회합니다.")
    @GetMapping("/get/searchMyPosts")
    public List<GetPostDto> getMyPosts(Authentication authentication) {
        return postService.searchByMyPost(authentication);

    }



    @PostMapping(value = "/post/posts", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    @Operation(summary = "글을 작성, Readme를 저장하는 API(AccessToken 필수)", description = "예시) api/post/posts?file=맘대로\n" +
            "Json을 넘기고 파일을 넘기면 S3에 readme를 저장하고, 글을 저장합니다")
    public ResponseEntity<Post> createPost(Authentication authentication,
                                           @ModelAttribute PostsDTO postsDTO,
                                           @RequestParam("file") MultipartFile file) throws IOException, TranscoderException {

        // Post 작성 서비스 호출
        return postService.createPost(authentication, postsDTO, file);
    }




    @Operation(summary = "모든 사용자의 전체 글을 조회하는 API")
    @GetMapping("/get/all/posts")
    public List<SelectAllPost> selectAllPost() {

        List<SelectAllPost> selectAllPosts = postService.selectAllPost();
        return selectAllPosts;
    }





    @Operation(summary = "사용자의 글 한 개를 조회하는 API", description = "예시) /api/get/posts?post_id=5를 URL로 호출하면 5번째 글을 조회합니다.")
    @GetMapping("/get/posts")
    public ResponseEntity<GetPostDto> selectPost(@RequestParam("post_id") Long post_id) {

        return postService.selectPost(post_id);
    }


    @Operation(summary = "글을 삭제하는 API(AccessToken 필수)", description = "예시) /api/delete/posts?post_id=5를하면 5번째 글을 삭제시킵니다.")
    @DeleteMapping("/delete/posts")
    public boolean deletePost(Authentication authentication, @RequestParam("post_id") Long post_id) {

        boolean deletePost = postService.deletePost(authentication, post_id);
        return deletePost;
    }

    @Operation(summary = "게시글 좋아요 API(AccessToken 필수)", description = "예시) /api/like/posts?post_id=5를 하면 만약 5번글에 좋아요를 처음 누르는 글이라면 true반환\n"+
    "이미 누른 글이라면 false를 반환합니다.")
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

    @Operation(summary = "게시글 무한스크롤 API", description = "예시)" +
            "/api/get/infinity/posts?page_number=1이면 마지막글부터 마지막글-10번까지의 글이 조회됩니다.")
    @GetMapping("/get/infinity/posts")
    public Slice<GetPostDto> getPosts(@RequestParam("page_number") Long page_number) {
        return postService.searchByPost(page_number, PageRequest.ofSize(10));
    }



    @Operation(summary = "게시글 목록에서 선택하면 상세보기하는 API", description = "예시)" +
            "/api/get/detail/post?post_id=5 5번게시글을 상세 조회할 수 있습니다")
    @GetMapping("/get/detail/post")
    public GetPostDto getDetailPost(Authentication authentication,
                              @RequestParam Long post_id) {
        return postService.getDetailPost(authentication, post_id);
    }

}
