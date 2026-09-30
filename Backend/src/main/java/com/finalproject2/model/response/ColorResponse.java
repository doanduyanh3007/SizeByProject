package com.finalproject2.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColorResponse {
    private Long id;
    private String name;
    private String hexCode;
    private String hexCode1;
    private Boolean isDeleted;
}
