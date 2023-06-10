package com.example.Pick_Read_Me;

import com.example.Pick_Read_Me.Domain.Dto.PostDto.PostsDTO;
import com.example.Pick_Read_Me.Repository.PostRepository;
import com.example.Pick_Read_Me.Service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.parameters.P;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("글 작성 ")
    public void postCreateTest() {
        //Given
        PostsDTO postsDTO = new PostsDTO("Test_title", "Test_Content", "Test_Repo");

    }
}
