package com.finalproject2.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SizeRequest {
    @NotBlank
    private String name;
}
