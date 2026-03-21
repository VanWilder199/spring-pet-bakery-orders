package buloshnaya.orders.kafka.dto;

import java.math.BigDecimal;

public record OrderItemDto(
    String productId,
    String productName,
    BigDecimal price,
    Integer quantity
) {}