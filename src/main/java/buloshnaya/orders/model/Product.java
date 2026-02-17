package buloshnaya.orders.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

public record Product(
        @Null
        Long id,
        @NotNull
        String productId,
        @NotNull
        String productName,
        @NotNull
        Integer price,
        @NotNull
        Integer quantity
) {
}
