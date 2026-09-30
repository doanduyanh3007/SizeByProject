package com.finalproject2.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalproject2.repository.ProductRepository;
import com.finalproject2.repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatbotService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String cachedSystemContext = null;
    private long lastCacheTime = 0;
    private static final long CACHE_TTL = 5 * 60 * 1000; // 5 minutes

    public ChatbotService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(15000);
        this.restTemplate = new RestTemplate(factory);
    }

    private String getProductInfoWithPrice(com.finalproject2.entity.Product p) {
        List<com.finalproject2.entity.ProductVariant> variants = productVariantRepository.findByProduct_Id(p.getId());
        String priceStr = "Chưa có giá";
        if (variants != null && !variants.isEmpty()) {
            priceStr = variants.get(0).getPrice() + " VNĐ";
        }
        return p.getName() + " (Mã: " + p.getProductCode() + " - ID: " + p.getId() + " - Giá: " + priceStr + ")";
    }

    private String getSystemContextBase() {
        if (cachedSystemContext != null && System.currentTimeMillis() - lastCacheTime < CACHE_TTL) {
            return cachedSystemContext;
        }

        List<com.finalproject2.entity.Product> topSelling = productRepository.getTopSellingProducts();
        List<com.finalproject2.entity.Product> topRated = productRepository.getTopRatedProducts();

        String topSellingStr = topSelling.stream().map(this::getProductInfoWithPrice).reduce((a, b) -> a + " | " + b).orElse("Không có");
        String topRatedStr = topRated.stream().map(this::getProductInfoWithPrice).reduce((a, b) -> a + " | " + b).orElse("Không có");

        cachedSystemContext = "Bạn là nhân viên tư vấn bán hàng của shop giày SizeBy. Hãy trả lời ngắn gọn, thân thiện bằng tiếng Việt. " +
                               "Đây là danh sách sản phẩm bán chạy nhất hiện tại: " + topSellingStr + ". " +
                               "Đây là danh sách sản phẩm được đánh giá cao nhất: " + topRatedStr + ". " +
                               "Dựa vào thông tin trên, hãy trả lời khách hàng thật tự nhiên. ĐẶC BIỆT QUAN TRỌNG: Nếu bạn nhắc đến bất kỳ sản phẩm nào trong câu trả lời, hãy luôn đính kèm chính xác chuỗi [PRODUCT:id] vào cuối câu trả lời (ví dụ: [PRODUCT:1] [PRODUCT:5]) để hệ thống hiển thị ảnh cho khách hàng.";
        lastCacheTime = System.currentTimeMillis();
        return cachedSystemContext;
    }

    public String getChatbotReply(String userMessage) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "Xin lỗi, chatbot hiện chưa được cấu hình API key.";
        }

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key=" + apiKey;

        try {
            String systemContext = getSystemContextBase() + "Câu hỏi: " + userMessage;

            Map<String, Object> parts = new HashMap<>();
            parts.put("text", systemContext);

            Map<String, Object> contents = new HashMap<>();
            contents.put("parts", List.of(parts));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(contents));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            return rootNode.path("candidates").get(0)
                           .path("content").path("parts").get(0)
                           .path("text").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "Xin lỗi, hiện tại tôi không thể trả lời do phản hồi chậm hoặc lỗi mạng. Vui lòng thử lại sau.";
        }
    }
}
