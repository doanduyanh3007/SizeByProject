package com.finalproject2.controller;

import com.finalproject2.model.request.AccountRequest;
import com.finalproject2.model.request.ForgotPasswordRequestRequest;
import com.finalproject2.model.request.ForgotPasswordResetRequest;
import com.finalproject2.model.request.ForgotPasswordVerifyRequest;
import com.finalproject2.model.request.LoginRequest;
import com.finalproject2.model.response.AccountResponse;
import com.finalproject2.model.response.LoginResponse;
import com.finalproject2.service.AccountService;
import com.finalproject2.service.UserAddressService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(originPatterns = "*")
public class AccountController {

    private final AccountService svc;
    private final UserAddressService addressService;

    public AccountController(AccountService svc, UserAddressService addressService) {
        this.svc = svc;
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> create(@Valid @RequestBody AccountRequest req) {
        return ResponseEntity.ok(svc.create(req));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAll() {
        return ResponseEntity.ok(svc.getAll());
    }

    @GetMapping("/employees")
    public ResponseEntity<List<AccountResponse>> getEmployees() {
        return ResponseEntity.ok(svc.getEmployees());
    }

    @GetMapping("{id}")
    public ResponseEntity<AccountResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(svc.getById(id));
    }

    @PutMapping("{id}")
    public ResponseEntity<AccountResponse> update(@PathVariable Integer id,
                                                  @Valid @RequestBody AccountRequest req) {
        req.setAccountCode(null);
        return ResponseEntity.ok(svc.update(id, req));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(svc.login(request));
    }

    @PostMapping("/google")
    public ResponseEntity<LoginResponse> loginWithGoogle(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Thiếu token xác thực");
        }
        return ResponseEntity.ok(svc.loginWithGoogle(token));
    }

    @PostMapping("/forgot-password/request")
    public ResponseEntity<Map<String, String>> requestForgotPasswordCode(
            @Valid @RequestBody ForgotPasswordRequestRequest request) {
        return ResponseEntity.ok(svc.requestPasswordResetCode(request.getGmail()));
    }

    @PostMapping("/forgot-password/verify")
    public ResponseEntity<Map<String, String>> verifyForgotPasswordCode(
            @Valid @RequestBody ForgotPasswordVerifyRequest request) {
        svc.verifyPasswordResetCode(request.getGmail(), request.getCode());
        return ResponseEntity.ok(Map.of("message", "Code is valid"));
    }

    @PostMapping("/forgot-password/reset")
    public ResponseEntity<Map<String, String>> resetForgotPassword(
            @Valid @RequestBody ForgotPasswordResetRequest request) {
        svc.resetPasswordWithCode(request.getGmail(), request.getCode(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password has been reset successfully"));
    }

    @PostMapping("{id}/upload-avatar")
    public ResponseEntity<Map<String, String>> uploadAvatar(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Only image files are allowed"));
        }

        if (file.getSize() > 2 * 1024 * 1024) {
            return ResponseEntity.badRequest().body(Map.of("error", "File size must be less than 2MB"));
        }

        try {
            String imageUrl = svc.uploadAvatar(id, file);
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("{id}/addresses")
    public ResponseEntity<List<Map<String, Object>>> getAddresses(@PathVariable Integer id) {
        return ResponseEntity.ok(addressService.getByAccountId(id));
    }

    @PostMapping("{id}/addresses")
    public ResponseEntity<Map<String, Object>> createAddress(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(addressService.create(id, payload));
    }

    @PutMapping("{id}/addresses/{addressId}")
    public ResponseEntity<Map<String, Object>> updateAddress(
            @PathVariable Integer id,
            @PathVariable Integer addressId,
            @RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(addressService.update(id, addressId, payload));
    }

    @DeleteMapping("{id}/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Integer id,
            @PathVariable Integer addressId) {
        addressService.delete(id, addressId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}/addresses/{addressId}/default")
    public ResponseEntity<Map<String, Object>> setDefaultAddress(
            @PathVariable Integer id,
            @PathVariable Integer addressId) {
        return ResponseEntity.ok(addressService.setDefault(id, addressId));
    }
}
