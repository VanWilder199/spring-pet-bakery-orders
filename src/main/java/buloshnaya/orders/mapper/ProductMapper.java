package buloshnaya.orders.mapper;

import buloshnaya.orders.entity.OrderItemEntity;
import buloshnaya.orders.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public OrderItemEntity toEntity(Product product) {
        OrderItemEntity productEntity = new OrderItemEntity();
        productEntity.setProductId(product.productId());
        productEntity.setProductName(product.productName());
        productEntity.setPrice(product.price());
        productEntity.setQuantity(product.quantity());
        return productEntity;

    }

    public Product toModel(OrderItemEntity productEntity) {
        return new Product(
                productEntity.getId(),
                productEntity.getProductId(),
                productEntity.getProductName(),
                productEntity.getPrice(),
                productEntity.getQuantity()
        );
    }
}
