package buloshnaya.orders.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.util.List;

public record Order(
        @Null
        String id,
        @NotNull
        List<Product> product,
        @NotNull
        String status
) {
}
