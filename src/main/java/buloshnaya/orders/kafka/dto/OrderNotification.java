package buloshnaya.orders.kafka.dto;

import java.util.List;
import java.util.UUID;

public record OrderNotification(
        UUID orderId,
    UUID userId,
    String email,
    NotificationType notificationType,
    List<OrderItemDto> items
) {}