package com.example.Pick_Read_Me.Domain.Dto;

import com.example.Pick_Read_Me.Domain.Refresh;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter
@Getter

public class OauthMemberDto {

    private Long id;
    private String name;
    private String email;
    private Date created;
    private String repo;
    private List<String> roles = new ArrayList<>();
}
