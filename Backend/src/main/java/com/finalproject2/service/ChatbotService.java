package com.finalproject2.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatbotService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getChatbotReply(String userMessage) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "Xin lỗi, chatbot hiện chưa được cấu hình API key.";
        }

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key=" + apiKey;

        try {
            Map<String, Object> parts = new HashMap<>();
            parts.put("text", "Bạn là nhân viên tư vấn bán hàng của shop giày SizeBy. Hãy trả lời ngắn gọn, thân thiện bằng tiếng Việt. Câu hỏi: " + userMessage);

            Map<String, Object> contents = new HashMap<>();
            contents.put("parts", List.of(parts));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(contents));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            String reply = rootNode.path("candidates").get(0)
                                   .path("content").path("parts").get(0)
                                   .path("text").asText();
            
            return reply;
        } catch (Exception e) {
            e.printStackTrace();
            return "Xin lỗi, hiện tại tôi không thể trả lời. Vui lòng thử lại sau.";
        }
    }
}
