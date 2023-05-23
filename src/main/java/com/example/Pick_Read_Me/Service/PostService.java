package com.example.Pick_Read_Me.Service;

import com.example.Pick_Read_Me.Domain.Dto.PostDto.PostsDTO;
import com.example.Pick_Read_Me.Domain.Entity.Member;
import com.example.Pick_Read_Me.Domain.Entity.Post;
import com.example.Pick_Read_Me.Exception.MemberNotFoundException;
import com.example.Pick_Read_Me.Repository.MemberRepository;
import com.example.Pick_Read_Me.Repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Slf4j
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;
    public String getReadMe(Long github_id, String repo_name) {
        Member member = memberRepository.findById(github_id).orElseGet(Member::new);
        RestTemplate restTemplate = new RestTemplate();

        String apiUrl = String.format("https://api.github.com/repos/"+member.getName()+"/"+
                repo_name+"/readme");

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Your-User-Agent"); // GitHub API 요청 시 User-Agent 헤더 필요

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseData = response.getBody();
            String content = (String) responseData.get("content");
            content = content.replace("\n", "");
            // Base64로 인코딩된 내용을 디코딩
            byte[] decodedBytes = Base64.getDecoder().decode(content);
            String decodedContent = new String(decodedBytes, StandardCharsets.UTF_8);

            return decodedContent;
        } else {
            // API 요청이 실패한 경우 에러 처리
            throw new RuntimeException("Failed to fetch README from GitHub API");
        }
    }

    public Post createPost(Long githubId, PostsDTO postsDTO) {
        Member member = memberRepository.findById(githubId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + githubId));

        Post post = new Post();
        post.setContent(postsDTO.getContent());
        post.setTitle(postsDTO.getTitle());
        post.setPostCreatedAt(new Date());
        post.setPostUpdatedAt(new Date());
        post.setRepo(postsDTO.getRepo());
        post.setMember(member);

        member.getPosts().add(post);

        postRepository.save(post);
        memberRepository.save(member);

        return post;
    }
}
