package com.finalproject2.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private boolean success;
    private String message;
    private AccountResponse account;
    private String token; // Nếu dùng JWT

    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public LoginResponse(boolean success, String message, AccountResponse account) {
        this.success = success;
        this.message = message;
        this.account = account;
    }
}