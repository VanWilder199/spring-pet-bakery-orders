package buloshnaya.orders.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.math.BigDecimal;

public record Product(
        @Null
        Long id,
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
