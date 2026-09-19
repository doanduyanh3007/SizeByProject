package com.finalproject2.mapper;

import jakarta.servlet.http.HttpServletRequest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class VNPayUtil {

    private VNPayUtil() {}

    public static String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] hashBytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hashBytes.length * 2);
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMAC-SHA512", e);
        }
    }

    private static String encodeVnp(String value) {
        return URLEncoder.encode(value, StandardCharsets.US_ASCII);
    }

    public static Map<String, String> sanitizeParams(Map<String, ?> params) {
        Map<String, String> cleaned = new TreeMap<>();
        if (params == null) {
            return cleaned;
        }
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            String key = entry.getKey();
            Object raw = entry.getValue();
            if (key == null || raw == null) {
                continue;
            }
            String value;
            if (raw instanceof String[] arr) {
                value = arr.length > 0 && arr[0] != null ? arr[0] : "";
            } else if (raw instanceof List<?> list) {
                value = list.isEmpty() || list.get(0) == null ? "" : String.valueOf(list.get(0));
            } else {
                value = String.valueOf(raw);
            }
            if (value.isEmpty() || "null".equals(value)) {
                continue;
            }
            // Vue Router uses decodeURIComponent, which keeps '+' instead of treating
            // it as a space like application/x-www-form-urlencoded.
            if (!"vnp_SecureHash".equals(key) && !"vnp_SecureHashType".equals(key)) {
                value = value.replace('+', ' ');
            }
            if ("vnp_SecureHash".equals(key) || "vnp_SecureHashType".equals(key)) {
                cleaned.put(key, value);
                continue;
            }
            if (key.startsWith("vnp_")) {
                cleaned.put(key, value);
            }
        }
        return cleaned;
    }

    public static String buildHashData(Map<String, String> params) {
        return joinFields(params, true);
    }

    public static String buildRawHashData(Map<String, String> params) {
        return joinFields(params, false);
    }

    private static String joinFields(Map<String, String> params, boolean encodeValues) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            if ("vnp_SecureHash".equals(fieldName) || "vnp_SecureHashType".equals(fieldName)) {
                continue;
            }
            String fieldValue = params.get(fieldName);
            if (fieldValue == null || fieldValue.isEmpty()) {
                continue;
            }
            if (hashData.length() > 0) {
                hashData.append('&');
            }
            hashData.append(fieldName)
                    .append('=')
                    .append(encodeValues ? encodeVnp(fieldValue) : fieldValue);
        }
        return hashData.toString();
    }

    public static String buildQueryString(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder query = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = params.get(fieldName);
            if (fieldValue == null || fieldValue.isEmpty()) {
                continue;
            }
            if (query.length() > 0) {
                query.append('&');
            }
            query.append(encodeVnp(fieldName)).append('=').append(encodeVnp(fieldValue));
        }
        return query.toString();
    }

    public static String buildPaymentUrl(String baseUrl, Map<String, String> params, String hashSecret) {
        String hashData = buildHashData(params);
        String queryString = buildQueryString(params);
        String secureHash = hmacSHA512(hashSecret, hashData);
        return baseUrl + "?" + queryString + "&vnp_SecureHash=" + secureHash;
    }

    public static boolean verifyHash(Map<String, ?> params, String hashSecret) {
        return verifyHash(params, hashSecret, null);
    }

    public static boolean verifyHash(Map<String, ?> params, String hashSecret, String rawQuery) {
        if (verifyRawQuery(rawQuery, hashSecret)) {
            return true;
        }
        Map<String, String> sanitized = sanitizeParams(params);
        String receivedHash = sanitized.remove("vnp_SecureHash");
        sanitized.remove("vnp_SecureHashType");
        if (receivedHash == null || receivedHash.isEmpty()) {
            return false;
        }

        String encodedData = joinFields(sanitized, true);
        String rawData = joinFields(sanitized, false);
        String encodedHash = hmacSHA512(hashSecret, encodedData);
        String rawHash = hmacSHA512(hashSecret, rawData);
        return receivedHash.equalsIgnoreCase(encodedHash) || receivedHash.equalsIgnoreCase(rawHash);
    }

    /**
     * Hash the original encoded query string from VNPay (minus SecureHash).
     * This avoids Vue/Spring decoding '+' vs '%20' mismatches.
     */
    public static boolean verifyRawQuery(String rawQuery, String hashSecret) {
        if (rawQuery == null || rawQuery.isBlank() || hashSecret == null) {
            return false;
        }
        String query = rawQuery.startsWith("?") ? rawQuery.substring(1) : rawQuery;
        Map<String, String> encodedFields = new TreeMap<>();
        String receivedHash = null;
        for (String pair : query.split("&")) {
            if (pair.isEmpty()) {
                continue;
            }
            int idx = pair.indexOf('=');
            String key = idx >= 0 ? pair.substring(0, idx) : pair;
            String value = idx >= 0 ? pair.substring(idx + 1) : "";
            if ("vnp_SecureHash".equals(key)) {
                receivedHash = value;
                continue;
            }
            if ("vnp_SecureHashType".equals(key) || key.isEmpty() || value.isEmpty()) {
                continue;
            }
            encodedFields.put(key, value);
        }
        if (receivedHash == null || receivedHash.isEmpty() || encodedFields.isEmpty()) {
            return false;
        }
        String hashData = joinFields(encodedFields, false);
        return receivedHash.equalsIgnoreCase(hmacSHA512(hashSecret, hashData));
    }

    public static Long parseOrderId(String txnRef) {
        if (txnRef == null || txnRef.isBlank()) {
            return null;
        }
        String idPart = txnRef.split("[_-]", 2)[0];
        return Long.valueOf(idPart);
    }

    public static Map<String, String> fromRequest(HttpServletRequest request) {
        Map<String, String> params = new LinkedHashMap<>();
        request.getParameterMap().forEach((key, values) -> {
            if (key != null && values != null && values.length > 0 && values[0] != null) {
                params.put(key, values[0]);
            }
        });
        return sanitizeParams(params);
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        if (ip == null || ip.isEmpty() || "::1".equals(ip) || "https://example.net/id/garnet".equals(ip)) {
            return "127.0.0.1";
        }
        return ip;
    }
}
