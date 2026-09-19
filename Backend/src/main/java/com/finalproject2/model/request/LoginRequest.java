package com.finalproject2.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @Email
    @NotBlank
    private String gmail;

    @NotBlank
    private String password;
}