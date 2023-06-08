package com.example.Pick_Read_Me;
import com.example.Pick_Read_Me.Config.SecurityConfig;
import com.example.Pick_Read_Me.Controller.PostController;
import com.example.Pick_Read_Me.Domain.Dto.PostDto.PostsDTO;
import com.example.Pick_Read_Me.Domain.Entity.Member;
import com.example.Pick_Read_Me.Domain.Entity.Post;
import com.example.Pick_Read_Me.Jwt.JwtProvider;
import com.example.Pick_Read_Me.Repository.MemberRepository;
import com.example.Pick_Read_Me.Repository.PostRepository;
import com.example.Pick_Read_Me.Service.CustomOAuth2UserService;
import com.example.Pick_Read_Me.Service.PostService;
import com.example.Pick_Read_Me.Util.CommonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletRequest;

import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.*;
@WebMvcTest(controllers = PostController.class)
// 2
@MockBeans({
        @MockBean(JpaMetamodelMappingContext.class),
        @MockBean(PostService.class),
        @MockBean(CustomOAuth2UserService.class),
        @MockBean(SecurityConfig.class),
        @MockBean(CommonUtil.class)
})


class PostControllerTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private PostsDTO postsDTO;

    @MockBean
    private JwtProvider jwtProvider;

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPost_shouldReturnCreatedPost() {
        // Mocking token and github_id
        Long githubId = 64027019L;
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJnaXRodWJJZCI6Ijk2NzEwNzMyIiwiaXNzIjoibmFtZSIsImV4cCI6MTY4NjEyODkyMCwiaWF0IjoxNjg2MTI4OTE5fQ.LLhoQ-hpraE3lGm3IOhLb-t8J_tK0GilgN2c2Fts1PE";

        // Mocking member
        Member member = new Member();
        member.setId(githubId);
        memberRepository.save(member);

        when(memberRepository.findById(githubId)).thenReturn(Optional.of(member));

        // Mocking postsDTO
        String content = "Test Content";
        String title = "Test Title";
        String repo = "Test Repo";
        when(postsDTO.getContent()).thenReturn(content);
        when(postsDTO.getTitle()).thenReturn(title);
        when(postsDTO.getRepo()).thenReturn(repo);

        // Mocking created post
        Post createdPost = new Post();
        when(request.getHeader("accessToken")).thenReturn(token);
        when(postService.createPost(request, postsDTO)).thenReturn(createdPost);

        // Execute the API endpoint
        ResponseEntity<Post> response = postController.createPost(request, postsDTO);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(createdPost, response.getBody());


        // Verify that postRepository.save() and memberRepository.save() were called
        verify(postRepository, times(1)).save(createdPost);
        verify(memberRepository, times(1)).save(member);
    }
}