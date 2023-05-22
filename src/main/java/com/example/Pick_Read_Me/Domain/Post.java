package com.example.Pick_Read_Me.Domain;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

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
    private Timestamp postCreatedAt;

    @Column(name = "post_update_at")
    private Timestamp postUpdatedAt;

    @ManyToOne()
    @JoinColumn(name = "member_id") //글을쓴 Member_id
    private Member member;
}
