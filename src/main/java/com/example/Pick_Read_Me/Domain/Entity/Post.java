package com.example.Pick_Read_Me.Domain.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor()
@Entity
public class Post {
    @Id
    @Column(name = "postId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "postCreatedAt", nullable = false)
    private Date postCreatedAt;

    @Column(name = "postUpdateAt", nullable = false)
    private Date postUpdatedAt;

    @Column(name = "repo", nullable = false)
    private String repo;

    @Column(name = "postLike")
    private Long post_like;

    @ManyToMany(mappedBy = "likedPosts")
    @JsonIgnore
    private List<Member> likedMembers = new ArrayList<>();


    @ManyToOne()
    @JoinColumn(name = "githubId") //글을쓴 Member_id
    private Member member;

    public void addLike(Member member) {
        if (!likedMembers.contains(member)) {
            System.out.println("멤버가 존재하지 않음 ");
            member.getLikedPosts().add(this);//좋아한 글 List에 해당 글의 객체 추가
            post_like++;
        }
    }

    public void removeLike(Member member) {
        if (likedMembers.contains(member)) {
            member.getLikedPosts().remove(this);
            post_like--;
        }
    }

}
