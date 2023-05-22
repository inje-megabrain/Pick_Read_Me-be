package com.example.Pick_Read_Me.Repository;

import com.example.Pick_Read_Me.Domain.Post;
import com.example.Pick_Read_Me.Domain.Refresh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

}