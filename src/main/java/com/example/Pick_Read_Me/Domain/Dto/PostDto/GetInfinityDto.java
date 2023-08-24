package com.example.Pick_Read_Me.Domain.Dto.PostDto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class GetInfinityDto {
    private Long id;

    private String title;

    private String content;

    private String repo;

    private Long post_like;

    private String owner;

    private Date create_time;
    private Date update_time;

    private String svgUrl;

    public GetInfinityDto(Long id, String title, String content, String repo, Long post_like, String owner,
                          Date create_time, Date update_time, String svgUrl) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.repo = repo;
        this.post_like = post_like;
        this.owner = owner;
        this.create_time = create_time;
        this.update_time = update_time;
        this.svgUrl = svgUrl;
    }



}
