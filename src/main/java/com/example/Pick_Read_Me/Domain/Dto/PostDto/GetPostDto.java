package com.example.Pick_Read_Me.Domain.Dto.PostDto;

import com.example.Pick_Read_Me.Domain.Entity.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
public class GetPostDto {
    private Long id;

    private String title;

    private String content;

    private Date postCreatedAt;

    private Date postUpdatedAt;

    private String repo;

    private Long post_like;

    private List<Member> likedMembers = new ArrayList<>();

    private Member member;

    public GetPostDto(Long id, String title, String content, Date postCreatedAt, Date postUpdatedAt, String repo, Long post_like, List<Member> likedMembers, Member member) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.postCreatedAt = postCreatedAt;
        this.postUpdatedAt = postUpdatedAt;
        this.repo = repo;
        this.post_like = post_like;
        this.likedMembers = likedMembers;
        this.member = member;
    }
}
