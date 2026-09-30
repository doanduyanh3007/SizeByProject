package com.finalproject2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {
    @Autowired
    private JavaMailSender mailSender;

    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();
    private final Map<String, Long> otpExpiry = new ConcurrentHashMap<>();

    public void sendOtp(String email) throws MessagingException {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStorage.put(email, otp);
        otpExpiry.put(email, System.currentTimeMillis() + 5 * 60 * 1000);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(email);
        helper.setSubject("SizeBy - Ma xac nhan don hang COD");
        helper.setText(buildHtmlEmail(otp), true);
        mailSender.send(message);
    }

    private String buildHtmlEmail(String otp) {
        return "<!DOCTYPE html>" +
            "<html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1'>" +
            "<style>" +
            "body{margin:0;padding:0;background:#f4f6fb;font-family:'Segoe UI',Arial,sans-serif;}" +
            ".wrapper{max-width:520px;margin:40px auto;background:#fff;border-radius:18px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,0.10);}" +
            ".header{background:#0a0a0a;padding:28px 32px;text-align:center;}" +
            ".header h1{margin:0;color:#fff;font-size:26px;font-weight:800;letter-spacing:3px;text-transform:uppercase;}" +
            ".header p{margin:4px 0 0;color:#aaa;font-size:12px;letter-spacing:1px;}" +
            ".body{padding:36px 36px 24px;}" +
            ".body h2{margin:0 0 10px;font-size:20px;color:#111;font-weight:700;}" +
            ".body p{margin:0 0 18px;color:#555;font-size:14px;line-height:1.7;}" +
            ".otp-box{background:#f0f4ff;border:2px dashed #4f6ef7;border-radius:14px;padding:22px 0;text-align:center;margin:24px 0;}" +
            ".otp-box .otp{font-size:42px;font-weight:900;letter-spacing:14px;color:#3b5bdb;font-family:monospace;}" +
            ".otp-box .expire{font-size:12px;color:#888;margin-top:8px;}" +
            ".warning{background:#fff8e1;border-left:4px solid #f59e0b;border-radius:6px;padding:12px 16px;margin:16px 0;}" +
            ".warning p{margin:0;color:#92400e;font-size:13px;font-weight:600;}" +
            ".footer{background:#f8f9fa;border-top:1px solid #eee;padding:20px 36px;text-align:center;}" +
            ".footer p{margin:0;color:#999;font-size:12px;line-height:1.8;}" +
            ".footer strong{color:#555;}" +
            "</style></head><body>" +
            "<div class='wrapper'>" +
            "  <div class='header'><h1>SizeBy</h1><p>Sneaker Store</p></div>" +
            "  <div class='body'>" +
            "    <h2>Xac nhan don hang COD</h2>" +
            "    <p>Xin chao,<br>Ban vua dat don hang thanh toan khi nhan hang (COD). " +
            "Vui long su dung ma xac nhan duoi day de hoan tat don hang cua ban:</p>" +
            "    <div class='otp-box'>" +
            "      <div class='otp'>" + otp + "</div>" +
            "      <div class='expire'>Ma nay se het han sau <strong>5 phut</strong></div>" +
            "    </div>" +
            "    <div class='warning'><p>Khong chia se ma nay cho bat ky ai.</p></div>" +
            "    <p>Neu ban khong thuc hien yeu cau nay, hay bo qua email nay.</p>" +
            "  </div>" +
            "  <div class='footer'>" +
            "    <p>Tran trong,<br><strong>Doi ngu SizeBy Sneaker Store</strong><br>" +
            "    Email nay duoc gui tu he thong tu dong, vui long khong phan hoi.</p>" +
            "  </div>" +
            "</div></body></html>";
    }

    public boolean verifyOtp(String email, String otp) {
        if (!otpStorage.containsKey(email)) return false;
        if (System.currentTimeMillis() > otpExpiry.getOrDefault(email, 0L)) {
            otpStorage.remove(email);
            otpExpiry.remove(email);
            return false;
        }
        if (otpStorage.get(email).equals(otp)) {
            otpStorage.remove(email);
            otpExpiry.remove(email);
            return true;
        }
        return false;
    }
}