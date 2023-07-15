package com.example.Pick_Read_Me.Domain.Dto.CustomSlice;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

public class CustomSliceImpl<T> extends SliceImpl<T> {

    private int customField;

    public CustomSliceImpl(List<T> content) {
        super(content);
    }

}
