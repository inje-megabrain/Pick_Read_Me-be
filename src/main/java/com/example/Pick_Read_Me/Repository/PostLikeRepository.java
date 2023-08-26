package com.example.Pick_Read_Me.Repository;

import com.example.Pick_Read_Me.Domain.Entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    List<PostLike> findByGithubId(Long githubId);

}
