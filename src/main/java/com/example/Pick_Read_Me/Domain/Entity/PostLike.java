package com.example.Pick_Read_Me.Domain.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class PostLike {

    @Id
    @Column(name = "githubId", nullable = false)
    private Long githubId;

    @Column(name = "postId")
    private Long post_id;
}