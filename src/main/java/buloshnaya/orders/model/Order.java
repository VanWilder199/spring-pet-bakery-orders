package buloshnaya.orders.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.util.List;
import java.util.UUID;

public record Order(
        @Null
        UUID id,
        @NotNull
        List<Product> product
) {
}
