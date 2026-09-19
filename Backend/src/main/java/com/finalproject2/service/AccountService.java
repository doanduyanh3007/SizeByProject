package com.finalproject2.service;

import com.finalproject2.model.request.AccountRequest;
import com.finalproject2.model.request.LoginRequest;
import com.finalproject2.model.response.AccountResponse;
import com.finalproject2.model.response.LoginResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface AccountService {

    AccountResponse create(AccountRequest req);

    AccountResponse update(Integer id, AccountRequest req);

    AccountResponse getById(Integer id);

    List<AccountResponse> getAll();

    List<AccountResponse> getEmployees();

    LoginResponse login(LoginRequest request);

    LoginResponse loginWithGoogle(String credentialToken);

    void delete(Integer id);

    String uploadAvatar(Integer id, MultipartFile file) throws IOException;

    Map<String, String> requestPasswordResetCode(String gmail);

    void verifyPasswordResetCode(String gmail, String code);

    void resetPasswordWithCode(String gmail, String code, String newPassword);
}
