package buloshnaya.orders.mapper;

import buloshnaya.orders.entity.OrderEntity;
import buloshnaya.orders.entity.OrderItemEntity;
import buloshnaya.orders.model.Order;
import buloshnaya.orders.model.Product;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    private final ProductMapper productMapper;

    public OrderMapper(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public OrderEntity toEntity(Order order) {
        List<OrderItemEntity> productEntities = order.product().stream().map(productMapper::toEntity).toList();

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderItemEntities(productEntities);
        productEntities.forEach(p -> p.setOrder(orderEntity));
        return orderEntity;
    }

    public Order toModel(OrderEntity orderEntity) {
        List<Product> productEntities = orderEntity.getOrderItemEntities().stream().map(productMapper::toModel).toList();

        return new Order(
                orderEntity.getId(),
                null,
                null,
                productEntities
        );
    }
}
