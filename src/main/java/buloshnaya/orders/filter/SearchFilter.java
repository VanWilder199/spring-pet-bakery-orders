package buloshnaya.orders.filter;

public record SearchFilter (
        Integer size,
        Integer page,
        String status
) {
    private static final int DEFAULT_SIZE = 10;
    private static final int DEFAULT_PAGE = 0;

    public SearchFilter {
        size = size != null ? size : DEFAULT_SIZE;
        page = page != null ? page : DEFAULT_PAGE;
    }
}
