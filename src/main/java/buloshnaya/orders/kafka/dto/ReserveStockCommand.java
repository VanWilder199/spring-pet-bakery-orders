package buloshnaya.orders.kafka.dto;

import java.util.List;
import java.util.UUID;

public record ReserveStockCommand(
        UUID orderId,
        UUID userId,
        UUID storeId,
        List<OrderItemDto> items
) {}