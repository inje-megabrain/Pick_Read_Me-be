package com.example.Pick_Read_Me.Domain.Dto.OAuthDto;

import lombok.Getter;
import lombok.Setter;

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
