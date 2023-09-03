package com.example.Pick_Read_Me.Domain.Dto.CustomSlice;

import com.example.Pick_Read_Me.Domain.Dto.PostDto.GetPostDto;
import lombok.Getter;

import java.util.List;

@Getter
public class CustomSliceResponseDto<T> {

    private List<GetPostDto> content;
    private Long nowPage;
    private Long totalPage;
    private int countContent;

    public CustomSliceResponseDto(List<GetPostDto> content, Long nowPage, Long totalPage,
                                  int countContent) {
        this.content = content;
        this.nowPage = nowPage;
        this.totalPage = totalPage;
        this.countContent = countContent;
    }

}
