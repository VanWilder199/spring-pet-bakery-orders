package buloshnaya.orders.mapper;

import buloshnaya.orders.entity.OrderEntity;
import buloshnaya.orders.kafka.dto.NotificationType;
import buloshnaya.orders.kafka.dto.OrderItemDto;
import buloshnaya.orders.kafka.dto.OrderNotification;
import buloshnaya.orders.model.Order;
import buloshnaya.orders.security.UserPrincipal;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderNotificationMapper {

    public OrderNotification toNotification(UserPrincipal userPrincipal, OrderEntity orderEntity, NotificationType type) {
        var items = orderEntity.getOrderItemEntities().stream()
                .map(e -> new OrderItemDto(e.getProductId(), e.getProductName(), e.getPrice(), e.getQuantity()))
                .toList();

        return new OrderNotification(
                orderEntity.getId(),
                userPrincipal.id(),
                userPrincipal.email(),
                type,
                items
        );
    }
}