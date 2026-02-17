package buloshnaya.orders.kafka.dto;

public record OrderItemDto(
    String productId,
    String productName,
    Integer price,
    Integer quantity
) {}