package buloshnaya.orders.filter;

import buloshnaya.orders.kafka.dto.NotificationType;

public record SearchFilter (
        Integer size,
        Integer page,
        NotificationType status
) {
    private static final int DEFAULT_SIZE = 10;
    private static final int DEFAULT_PAGE = 0;
    private static final NotificationType DEFAULT_STATUS = NotificationType.CONFIRMED;

    public SearchFilter {
        size = size != null ? size : DEFAULT_SIZE;
        page = page != null ? page : DEFAULT_PAGE;
        status = status != null ? status : DEFAULT_STATUS;
    }
}
