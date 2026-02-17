package buloshnaya.orders.web;

import java.time.LocalDateTime;

public record ErrorResponseDto (
        String message,
        String code,
        LocalDateTime timestamp
) {}