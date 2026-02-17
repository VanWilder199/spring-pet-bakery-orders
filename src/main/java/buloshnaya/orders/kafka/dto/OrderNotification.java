package buloshnaya.orders.kafka.dto;

import java.util.List;

public record OrderNotification(
    String orderId,
    List<OrderItemDto> items
) {}