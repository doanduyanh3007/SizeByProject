package com.finalproject2.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountRequest {

    private String accountCode; // allow setting/updating account code

    private String username;

    @Email
    @NotBlank
    private String gmail;

    private String phone;


    private String password;

    private String address;

    private Boolean isActive;

    // New field for image URL (nullable)
    private String imgUrl;

    private String role;
}
