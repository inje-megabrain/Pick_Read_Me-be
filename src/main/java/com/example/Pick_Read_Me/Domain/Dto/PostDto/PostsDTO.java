package com.example.Pick_Read_Me.Domain.Dto.PostDto;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class PostsDTO {

    private String title;

    private String content;

    private String repo;

    public PostsDTO(String title, String content, String repo) {
        this.title = title;
        this.content = content;
        this.repo = repo;
    }
}
