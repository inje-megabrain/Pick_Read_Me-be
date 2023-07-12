package com.example.Pick_Read_Me;

/*
import com.example.Pick_Read_Me.Controller.PostController;
import com.example.Pick_Read_Me.Domain.Dto.PostDto.PostsDTO;
import com.example.Pick_Read_Me.Domain.Entity.Post;
import com.example.Pick_Read_Me.Service.PostService;
import com.example.Pick_Read_Me.Util.CommonUtil;
import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.assertj.core.api.BDDAssumptions.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)

class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;
    @MockBean
    private CommonUtil commonUtil;

    @Autowired
    private Gson gson;

    @Test
    @DisplayName("글쓰기 Title Null Test")
    public void 글작성_테스트() throws Exception {
        // Given
        PostsDTO postsDTO = new PostsDTO( "","Test_Content", "Test_Repo");
        String content = gson.toJson(postsDTO);

        // Mock Post 객체 생성
        Post createdPost = new Post();
        createdPost.setId(1L);
        createdPost.setTitle("");
        createdPost.setContent("Test_Content");
        createdPost.setRepo("Test_Repo");

        ResponseEntity<Post> responseCreatePostDto = ResponseEntity.badRequest().body(createdPost);


        Authentication authentication = new UsernamePasswordAuthenticationToken("96710732", "sleeg00",
                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
        // SecurityContextHolder를 사용하여 SecurityContext에 인증 객체를 설정한다.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Mock postService.createPost()의 반환값 설정
        when(postService.createPost(any(Authentication.class), any(PostsDTO.class)))
                .thenReturn(responseCreatePostDto);

        // When
        mockMvc.perform(post("/api/posts")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").value(createdPost.getId()))
                .andExpect(jsonPath("$.title").value(postsDTO.getTitle()))
                .andExpect(jsonPath("$.content").value(postsDTO.getContent()))
                .andExpect(jsonPath("$.repo").value(postsDTO.getRepo()));

        // Then
        verify(postService, times(1)).createPost(any(Authentication.class),
                any(PostsDTO.class));
    }



}*/
