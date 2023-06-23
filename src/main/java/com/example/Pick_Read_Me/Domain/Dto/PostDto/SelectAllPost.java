package com.example.Pick_Read_Me.Domain.Dto.PostDto;

import com.example.Pick_Read_Me.Domain.Entity.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
public class SelectAllPost {

    private Long id;
    private String title;

    private String content;

    private Date postUpdatedAt;

    private String repo;

    private Long post_like;

    private String memberName;

    public SelectAllPost(Long id, String title, String content, Date postUpdatedAt, String repo, Long post_like, String memberName) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.postUpdatedAt = postUpdatedAt;
        this.repo = repo;
        this.post_like = post_like;
        this.memberName = memberName;
    }
}
