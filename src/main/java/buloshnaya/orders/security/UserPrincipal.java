package buloshnaya.orders.security;

import java.util.UUID;

public record UserPrincipal(
        UUID id,
        String role,
        String email
) {
}
