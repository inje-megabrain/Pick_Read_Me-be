package com.example.Pick_Read_Me.Repository;

import com.example.Pick_Read_Me.Domain.Refresh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshRepository extends JpaRepository<Refresh, Long> {

}


