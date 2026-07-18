package com.finalproject2.SecurityConfiguration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VNPayConfig {

    @Value("${vnpay.tmn-code:5UCZ2YPP}")
    private String vnpTmnCode;

    @Value("${vnpay.hash-secret:DB1CAI1PUWUGR1OYTAEJ9182J02Q03GI}")
    private String vnpHashSecret;

    @Value("${vnpay.pay-url:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}")
    private String vnpPayUrl;

    @Value("${vnpay.return-url:http://localhost:3002/payment/vnpay-return}")
    private String vnpReturnUrl;

    @Value("${vnpay.api-url:https://sandbox.vnpayment.vn/merchant_webapi/api/transaction}")
    private String vnpApiUrl;

    public String getVnpTmnCode() { return vnpTmnCode; }
    public String getVnpHashSecret() { return vnpHashSecret; }
    public String getVnpPayUrl() { return vnpPayUrl; }
    public String getVnpReturnUrl() { return vnpReturnUrl; }
    public String getVnpApiUrl() { return vnpApiUrl; }
}
