package buloshnaya.orders.model;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record Product(
        @NotNull
        String productId,
        @NotNull
        String productName,
        @NotNull
        BigDecimal price,
        @NotNull
        Integer quantity
) {
}
