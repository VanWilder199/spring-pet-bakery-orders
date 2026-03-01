package buloshnaya.orders.mapper;

import buloshnaya.orders.entity.OrderEntity;
import buloshnaya.orders.kafka.dto.NotificationType;
import buloshnaya.orders.kafka.dto.OrderItemDto;
import buloshnaya.orders.kafka.dto.OrderNotification;
import buloshnaya.orders.model.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderNotificationMapper {

    public OrderNotification toNotification(OrderEntity orderEntity, Order order, NotificationType type) {
        var items = orderEntity.getOrderItemEntities().stream()
                .map(e -> new OrderItemDto(e.getProductId(), e.getProductName(), e.getPrice(), e.getQuantity()))
                .toList();

        return new OrderNotification(
                orderEntity.getId(),
                order.userId(),
                order.email(),
                type,
                items
        );
    }
}