package com.finalproject2.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryResponse {
    private Long id;
    private String name;
    private String imageUrl;
    private Boolean isDeleted;
}