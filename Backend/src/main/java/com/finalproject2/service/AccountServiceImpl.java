package com.finalproject2.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.finalproject2.entity.Account;
import com.finalproject2.exception.NotFoundException;
import com.finalproject2.mapper.MapperUtil;
import com.finalproject2.model.request.AccountRequest;
import com.finalproject2.model.request.LoginRequest;
import com.finalproject2.model.response.AccountResponse;
import com.finalproject2.model.response.LoginResponse;
import com.finalproject2.repository.AccountRepository;
import com.finalproject2.repository.RoleRepository;
import com.finalproject2.SecurityConfiguration.JwtUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import java.util.Collections;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {

    private static final long RESET_CODE_TTL_SECONDS = 10 * 60;

    private final AccountRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final Cloudinary cloudinary;
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String mailUsername;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, ResetCodeState> resetCodeStore = new ConcurrentHashMap<>();
    @Value("${google.client.id}")
    private String googleClientId;
    private final RoleRepository roleRepo;
    private final JwtUtil jwtUtil;

    private static class ResetCodeState {
        private final String code;
        private final Instant expiresAt;

        private ResetCodeState(String code, Instant expiresAt) {
            this.code = code;
            this.expiresAt = expiresAt;
        }
    }

    public AccountServiceImpl(AccountRepository repo,
                              PasswordEncoder passwordEncoder,
                              RoleRepository roleRepo,
                              Cloudinary cloudinary,
                              JavaMailSender mailSender,
                              JwtUtil jwtUtil) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.roleRepo = roleRepo;
        this.cloudinary = cloudinary;
        this.mailSender = mailSender;
        this.jwtUtil = jwtUtil;
    }

    private AccountResponse toResponse(Account account) {
        AccountResponse response = MapperUtil.map(account, AccountResponse.class);

        boolean isAdmin = account.getRoles() != null &&
                account.getRoles().stream()
                        .anyMatch(r -> r != null && "ADMIN".equalsIgnoreCase(r.getName()));

        boolean isStaff = account.getRoles() != null &&
                account.getRoles().stream()
                        .anyMatch(r -> r != null && "STAFF".equalsIgnoreCase(r.getName()));

        boolean isUser = account.getRoles() != null &&
                account.getRoles().stream()
                        .anyMatch(r -> r != null && "USER".equalsIgnoreCase(r.getName()));

        boolean isGuest = account.getRoles() != null &&
                account.getRoles().stream()
                        .anyMatch(r -> r != null && "GUEST".equalsIgnoreCase(r.getName()));

        if (isAdmin) {
            response.setRole("ADMIN");
        } else if (isStaff) {
            response.setRole("STAFF");
        } else if (isUser) {
            response.setRole("USER");
        } else if (isGuest) {
            response.setRole("GUEST");
        } else {
            response.setRole("USER");
        }

        return response;
    }

    private String normalizeEmail(String gmail) {
        return gmail == null ? "" : gmail.trim().toLowerCase();
    }

    private String generateResetCode() {
        int codeValue = secureRandom.nextInt(900000) + 100000;
        return String.valueOf(codeValue);
    }

    private void sendResetCodeEmail(String gmail, String code, Instant expiresAt) {
        long minutesLeft = Math.max(1, (expiresAt.getEpochSecond() - Instant.now().getEpochSecond()) / 60);
        String subject = "Peak Seven | Mã xác minh đặt lại mật khẩu";
        String htmlBody = buildResetPasswordEmailHtml(code, minutesLeft);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(mailUsername);
            helper.setTo(gmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Không thể tạo email đặt lại mật khẩu", e);
        }
    }

    private String buildResetPasswordEmailHtml(String code, long minutesLeft) {
        String template = """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                    <title>Peak Seven - Đặt lại mật khẩu</title>
                </head>
                <body style="margin:0;padding:0;background:#f6efe8;font-family:'Segoe UI','Helvetica Neue',Tahoma,Arial,'Noto Sans',sans-serif;color:#201a17;">
                    <div style="padding:32px 16px;">
                        <table role="presentation" cellpadding="0" cellspacing="0" border="0" width="100%" style="max-width:640px;margin:0 auto;background:#fffdfb;border-radius:24px;overflow:hidden;box-shadow:0 18px 48px rgba(48,31,18,0.12);">
                            <tr>
                                <td style="background:linear-gradient(135deg,#231712 0%,#b85c2c 55%,#e6a77f 100%);padding:32px 36px;color:#ffffff;">
                                    <div style="font-size:12px;letter-spacing:0.18em;text-transform:uppercase;font-weight:700;opacity:0.88;">Peak Seven</div>
                                    <h1 style="margin:14px 0 10px;font-size:30px;line-height:1.15;font-weight:800;">Yêu cầu đặt lại mật khẩu</h1>
                                    <p style="margin:0;font-size:15px;line-height:1.7;color:#f9e7da;max-width:460px;">Chúng tôi đã nhận được yêu cầu khôi phục tài khoản của bạn. Hãy dùng mã xác minh bên dưới để tiếp tục đổi mật khẩu.</p>
                                </td>
                            </tr>
                            <tr>
                                <td style="padding:32px 36px 14px;">
                                    <p style="margin:0 0 14px;font-size:16px;line-height:1.7;color:#4e4037;">Xin chào,</p>
                                    <p style="margin:0 0 18px;font-size:16px;line-height:1.7;color:#4e4037;">Đây là mã xác minh dành cho yêu cầu đặt lại mật khẩu của bạn. Mã này chỉ có hiệu lực trong <strong>__MINUTES__ phút</strong>.</p>
                                    <div style="margin:26px 0;padding:24px;border-radius:20px;background:#fff4ec;border:1px solid #f0d4c1;text-align:center;">
                                        <div style="font-size:12px;text-transform:uppercase;letter-spacing:0.2em;color:#a35a32;font-weight:700;margin-bottom:10px;">Mã xác minh</div>
                                        <div style="font-size:36px;line-height:1;font-weight:800;letter-spacing:0.24em;color:#ba5c2d;">__CODE__</div>
                                    </div>
                                    <table role="presentation" cellpadding="0" cellspacing="0" border="0" width="100%" style="margin:0 0 18px;background:#f8f3ee;border-radius:18px;">
                                        <tr>
                                            <td style="padding:18px 20px;">
                                                <p style="margin:0 0 8px;font-size:14px;font-weight:700;color:#201a17;">Lưu ý bảo mật</p>
                                                <p style="margin:0;font-size:14px;line-height:1.7;color:#5e5148;">Không chia sẻ mã này với bất kỳ ai. Nếu bạn không thực hiện yêu cầu này, bạn có thể bỏ qua email và mật khẩu hiện tại vẫn được giữ nguyên.</p>
                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td style="padding:0 36px 30px;">
                                                    <div style="height:1px;background:#efe2d8;margin-bottom:22px;"></div>
                                                    <p style="margin:0 0 8px;font-size:14px;color:#6e6158;line-height:1.7;">Email này được gửi tự động từ hệ thống Peak Seven. Vui lòng không trả lời trực tiếp email này.</p>
                                                    <p style="margin:0;font-size:13px;color:#9a8a80;line-height:1.7;">Peak Seven | Thời trang thể thao và giày phong cách hiện đại</p>
                                                </td>
                                            </tr>
                                        </table>
                                    </div>
                                </body>
                                </html>
                                """;

        return template
                .replace("__MINUTES__", String.valueOf(minutesLeft))
                .replace("__CODE__", code);
    }

    private ResetCodeState getValidResetState(String gmail, String code) {
        String key = normalizeEmail(gmail);
        ResetCodeState state = resetCodeStore.get(key);

        if (state == null) {
            throw new RuntimeException("No reset code found. Please request a new code.");
        }

        if (Instant.now().isAfter(state.expiresAt)) {
            resetCodeStore.remove(key);
            throw new RuntimeException("Reset code has expired. Please request a new code.");
        }

        if (!Objects.equals(state.code, code)) {
            throw new RuntimeException("Reset code is invalid.");
        }

        return state;
    }

    @Override
    @Transactional
    public AccountResponse create(AccountRequest req) {
        if (repo.findByGmail(req.getGmail()).isPresent()) {
            throw new RuntimeException("Email already exists: " + req.getGmail());
        }

        Account acc = new Account();
        MapperUtil.mapToExisting(req, acc);
        acc.setPasswordHash(passwordEncoder.encode(req.getPassword()));

        if (acc.getAccountCode() == null || acc.getAccountCode().isEmpty()) {
            acc.setAccountCode("ACC" + System.currentTimeMillis());
        }
        if (req.getImgUrl() != null) {
            acc.setImgUrl(req.getImgUrl());
        }

        String roleName = (req.getRole() != null && !req.getRole().isEmpty()) ? req.getRole().toUpperCase() : "USER";
        com.finalproject2.entity.Role role = roleRepo.findByName(roleName).orElse(null);
        if (role == null && !roleName.equals("USER")) {
            role = roleRepo.findByName("USER").orElse(null);
        }
        if (role != null) {
            if (acc.getRoles() == null) {
                acc.setRoles(new java.util.LinkedHashSet<>());
            }
            acc.getRoles().add(role);
        }

        acc = repo.save(acc);
        return toResponse(acc);
    }

    @Override
    @Transactional
    public AccountResponse update(Integer id, AccountRequest req) {
        Account acc = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Account not found with id: " + id));

        if (req.getGmail() != null && !req.getGmail().equals(acc.getGmail())) {
            if (repo.findByGmail(req.getGmail()).isPresent()) {
                throw new RuntimeException("Email already exists: " + req.getGmail());
            }
        }

        if (req.getUsername() != null) acc.setUsername(req.getUsername());
        if (req.getGmail() != null) acc.setGmail(req.getGmail());
        if (req.getPhone() != null) acc.setPhone(req.getPhone());
        if (req.getAddress() != null) acc.setAddress(req.getAddress());
        if (req.getImgUrl() != null) acc.setImgUrl(req.getImgUrl());
        if (req.getIsActive() != null) acc.setIsActive(req.getIsActive());

        if (req.getPassword() != null && !req.getPassword().isEmpty()) {
            acc.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        }

        if (req.getRole() != null && !req.getRole().isEmpty()) {
            com.finalproject2.entity.Role newRole = roleRepo.findByName(req.getRole())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy role: " + req.getRole()));
            acc.getRoles().clear();
            acc.getRoles().add(newRole);
        }

        acc = repo.save(acc);
        return toResponse(acc);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getById(Integer id) {
        Account acc = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Account not found with id: " + id));
        return toResponse(acc);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getAll() {
        return repo.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getEmployees() {
        return repo.findAll().stream()
                .filter(acc -> {
                    if (acc.getRoles() == null) return false;
                    return acc.getRoles().stream()
                            .anyMatch(r -> "ADMIN".equalsIgnoreCase(r.getName()) || "STAFF".equalsIgnoreCase(r.getName()));
                })
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Account acc = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Account not found with id: " + id));
        repo.delete(acc);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Account account = repo.findByGmail(request.getGmail())
                .orElseThrow(() -> new RuntimeException("Account not found with email: " + request.getGmail()));

        boolean matches = passwordEncoder.matches(request.getPassword(), account.getPasswordHash());
        if (!matches) {
            throw new RuntimeException("Invalid password");
        }

        if (account.getIsActive() != null && !account.getIsActive()) {
            throw new RuntimeException("Account is deactivated");
        }

        AccountResponse accountResponse = toResponse(account);
        String role = "CUSTOMER";
        if (account.getRoles() != null && !account.getRoles().isEmpty()) {
            role = account.getRoles().iterator().next().getName();
        }
        String token = jwtUtil.generateToken(account.getGmail(), role);
        return new LoginResponse(true, "Login successful", accountResponse, token);
    }

    @Override
    @Transactional
    public LoginResponse loginWithGoogle(String credentialToken) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(credentialToken);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                String email = payload.getEmail();
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");

                Account account = repo.findByGmail(email).orElse(null);

                if (account == null) {
                    account = new Account();
                    account.setGmail(email);
                    account.setUsername(name);
                    account.setAccountCode("ACC" + System.currentTimeMillis());

                    String randomPassword = UUID.randomUUID().toString();
                    account.setPasswordHash(passwordEncoder.encode(randomPassword));

                    account.setImgUrl(pictureUrl);
                    account.setIsActive(true);
                    account = repo.save(account);
                } else {
                    if (account.getIsActive() != null && !account.getIsActive()) {
                        throw new RuntimeException("Tài khoản của bạn đã bị khóa!");
                    }
                    if (pictureUrl != null) {
                        account.setImgUrl(pictureUrl);
                        account = repo.save(account);
                    }
                }

                // 4. Trả về Response giống hệt đăng nhập bằng mật khẩu
                AccountResponse accountResponse = toResponse(account);
                String role = "CUSTOMER";
                if (account.getRoles() != null && !account.getRoles().isEmpty()) {
                    role = account.getRoles().iterator().next().getName();
                }
                String jwtToken = jwtUtil.generateToken(account.getGmail(), role);
                return new LoginResponse(true, "Đăng nhập bằng Google thành công", accountResponse, jwtToken);

            } else {
                throw new RuntimeException("Mã xác thực Google không hợp lệ!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi xác thực Google: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> requestPasswordResetCode(String gmail) {
        Account account = repo.findByGmail(gmail)
                .orElseThrow(() -> new RuntimeException("Account not found with email: " + gmail));

        if (account.getIsActive() != null && !account.getIsActive()) {
            throw new RuntimeException("Account is deactivated");
        }

        String code = generateResetCode();
        Instant expiresAt = Instant.now().plusSeconds(RESET_CODE_TTL_SECONDS);
        resetCodeStore.put(normalizeEmail(gmail), new ResetCodeState(code, expiresAt));

        sendResetCodeEmail(account.getGmail(), code, expiresAt);

        return Map.of(
                "message", "Reset code has been sent to your email",
                "expiresAt", expiresAt.toString()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public void verifyPasswordResetCode(String gmail, String code) {
        getValidResetState(gmail, code);
    }

    @Override
    @Transactional
    public void resetPasswordWithCode(String gmail, String code, String newPassword) {
        getValidResetState(gmail, code);

        Account account = repo.findByGmail(gmail)
                .orElseThrow(() -> new RuntimeException("Account not found with email: " + gmail));

        account.setPasswordHash(passwordEncoder.encode(newPassword));
        repo.save(account);
        resetCodeStore.remove(normalizeEmail(gmail));
    }

    @Override
    @Transactional
    public String uploadAvatar(Integer id, MultipartFile file) throws IOException {
        Account acc = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Account not found with id: " + id));

        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = String.valueOf(uploadResult.get("secure_url"));

            acc.setImgUrl(imageUrl);
            repo.save(acc);

            return imageUrl;
        } catch (IOException e) {
            throw new IOException("Loi khi tai anh len Cloudinary: " + e.getMessage(), e);
        }
    }
}

