package buloshnaya.orders.mapper;

import buloshnaya.orders.entity.OrderEntity;
import buloshnaya.orders.kafka.dto.NotificationType;
import buloshnaya.orders.kafka.dto.OrderItemDto;
import buloshnaya.orders.kafka.dto.OrderNotification;
import buloshnaya.orders.model.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderNotificationMapper {

    public OrderNotification toNotification(UUID userId, OrderEntity orderEntity, NotificationType type) {
        var items = orderEntity.getOrderItemEntities().stream()
                .map(e -> new OrderItemDto(e.getProductId(), e.getProductName(), e.getPrice(), e.getQuantity()))
                .toList();

        // after implement userService we can get email by id
        return new OrderNotification(
                orderEntity.getId(),
                userId,
                null,
                type,
                items
        );
    }
}