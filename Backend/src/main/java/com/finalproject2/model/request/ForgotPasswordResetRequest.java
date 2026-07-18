package com.finalproject2.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordResetRequest {

    @Email
    @NotBlank
    private String gmail;

    @NotBlank
    private String code;

    @NotBlank
    @Size(min = 6)
    private String newPassword;
}
