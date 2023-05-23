package com.example.Pick_Read_Me.Domain.Entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor()
@Entity
public class Post {
    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "post_created_at")
    private Date postCreatedAt;

    @Column(name = "post_update_at")
    private Date postUpdatedAt;

    @Column(name = "repo")
    private String repo;
    @ManyToOne()
    @JoinColumn(name = "member_id") //글을쓴 Member_id
    private Member member;
}
