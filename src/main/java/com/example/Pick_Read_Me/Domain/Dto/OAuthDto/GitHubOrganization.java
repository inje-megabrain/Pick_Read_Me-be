package com.example.Pick_Read_Me.Domain.Dto.OAuthDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GitHubOrganization {
    private String login;
    private String description;
    // 필요한 다른 필드들...

    // 생성자, getter/setter 메서드 등...
}