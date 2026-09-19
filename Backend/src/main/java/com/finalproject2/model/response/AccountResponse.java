package com.finalproject2.model.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class AccountResponse {

    private Integer id;
    private String accountCode;
    private String username;
    private String gmail;
    private String phone;
    private String address;
    private Boolean isActive;
    private String imgUrl;
    private String role;
}
