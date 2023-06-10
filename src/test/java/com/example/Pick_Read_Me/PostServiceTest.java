package com.example.Pick_Read_Me;

import com.example.Pick_Read_Me.Controller.PostController;
import com.example.Pick_Read_Me.Domain.Dto.OAuthDto.GetMemberDto;
import com.example.Pick_Read_Me.Domain.Dto.PostDto.PostsDTO;
import com.example.Pick_Read_Me.Domain.Entity.Post;
import com.example.Pick_Read_Me.Repository.PostRepository;
import com.example.Pick_Read_Me.Service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;

import static org.assertj.core.api.BDDAssumptions.given;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("글 작성 ")
    public void postCreateTest() {
        //Given
        PostsDTO postsDTO = new PostsDTO("Test_Title", "Test_Content", "Test_Repo");
        // 인증 객체를 생성한다.
        Authentication authentication = new UsernamePasswordAuthenticationToken("96710732", "");
        // SecurityContextHolder를 사용하여 SecurityContext에 인증 객체를 설정한다.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        given(postService.createPost(Mockito.any(Post.class))).willReturn(new Post());

    }
}
