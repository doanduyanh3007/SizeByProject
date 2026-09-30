package com.finalproject2.service;

import com.finalproject2.entity.Order;
import com.finalproject2.entity.OrderItem;
import com.finalproject2.entity.ProductVariant;
import com.finalproject2.repository.OrderRepository;
import com.finalproject2.repository.ProductVariantRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class OrderCleanupService {

    private final OrderRepository orderRepo;
    private final ProductVariantRepository variantRepo;

    public OrderCleanupService(OrderRepository orderRepo, ProductVariantRepository variantRepo) {
        this.orderRepo = orderRepo;
        this.variantRepo = variantRepo;
    }

    // Chạy mỗi 5 phút (300,000 ms)
    @Scheduled(fixedRate = 300000)
    @Transactional
    public void cleanupPendingOnlineOrders() {
        // Tìm các đơn hàng PENDING cách đây hơn 20 phút
        Instant cutoff = Instant.now().minus(20, ChronoUnit.MINUTES);
        List<Order> deadOrders = orderRepo.findPendingOnlineOrders(cutoff);

        if (deadOrders.isEmpty()) {
            return;
        }

        System.out.println("[CRON] Tìm thấy " + deadOrders.size() + " đơn hàng VNPay bị treo. Đang tiến hành huỷ và hoàn kho...");

        for (Order order : deadOrders) {
            order.setStatus("CANCELLED");
            order.setReturnReason("Hệ thống tự động huỷ do không thanh toán VNPay trong thời gian quy định (20 phút)");
            orderRepo.save(order);

            // Hoàn lại kho
            if (order.getOrderItems() != null) {
                for (OrderItem item : order.getOrderItems()) {
                    ProductVariant variant = item.getVariant();
                    if (variant != null) {
                        int currentStock = variant.getStockQuantity() != null ? variant.getStockQuantity() : 0;
                        int itemQty = item.getQuantity() != null ? item.getQuantity() : 0;
                        variant.setStockQuantity(currentStock + itemQty);
                        variantRepo.save(variant);
                    }
                }
            }
        }
        System.out.println("[CRON] Đã xử lý xong các đơn hàng treo.");
    }
}
