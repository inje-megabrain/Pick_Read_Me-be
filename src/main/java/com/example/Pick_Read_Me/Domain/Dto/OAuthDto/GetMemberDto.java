package com.example.Pick_Read_Me.Domain.Dto.OAuthDto;

import com.example.Pick_Read_Me.Domain.Entity.Refresh;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

@Getter
public class GetMemberDto {

    private String name;

    private String repo;

    private String profile;

    private String email;

    public GetMemberDto(String name, String repo, String profile, String email) {
        this.name = name;
        this.repo = repo;
        this.profile = profile;
        this.email = email;
    }
}
