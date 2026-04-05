package buloshnaya.orders.service;

import buloshnaya.orders.entity.OrderEntity;
import buloshnaya.orders.entity.OutBoxEventEntity;
import buloshnaya.orders.filter.SearchFilter;
import buloshnaya.orders.kafka.dto.NotificationType;
import buloshnaya.orders.kafka.dto.OrderItemDto;
import buloshnaya.orders.kafka.dto.OrderNotification;
import buloshnaya.orders.kafka.dto.ReserveStockCommand;
import buloshnaya.orders.mapper.OrderMapper;
import buloshnaya.orders.mapper.OrderNotificationMapper;
import buloshnaya.orders.model.Order;
import buloshnaya.orders.repository.OrderRepository;
import buloshnaya.orders.repository.OutBoxEventRepository;
import buloshnaya.orders.util.JsonUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OutBoxEventRepository outBoxEventRepository;
    private final OrderMapper mapper;
    private final OrderNotificationMapper notificationMapper;
    private final JsonUtil jsonUtil;


    @Transactional(readOnly = true)
    public Page<Order> searchOrderByFilter(UUID userId, SearchFilter filter) {
         Pageable pageable = Pageable.ofSize(filter.size()).withPage(filter.page());

         logger.info("Searching orders with filter: {}", filter);

        return orderRepository.searchAllByFilter(userId, pageable, filter.status())
                .map(mapper::toModel);
    }

    @Transactional(readOnly = true)
    public Page<Order> searchOrderByFilterForAdmin(UUID userId, SearchFilter filter) {
        Pageable pageable = Pageable.ofSize(filter.size()).withPage(filter.page());

        logger.info("Searching orders with filter by Admin: {}", filter);

        return orderRepository.searchAllByFilter(userId, pageable, filter.status())
                .map(mapper::toModel);
    }

    @Transactional
    public Order createOrder(UUID userId, Order order) {
        OrderEntity mapperEntity = mapper.toEntity(order);
        mapperEntity.setUserId(userId);
        mapperEntity.setStatus(NotificationType.CONFIRMED);

        logger.info("Creating order: {}", mapperEntity);

        OrderEntity savedEntityOrder = orderRepository.save(mapperEntity);

        logger.info("savedEntityOrder order: {}", savedEntityOrder.toString());

        OrderNotification orderNotification = notificationMapper.toNotification(
                userId,
                savedEntityOrder, NotificationType.CONFIRMED);

        OutBoxEventEntity notificationEvent = new OutBoxEventEntity();
        notificationEvent.setOrderId(savedEntityOrder.getId());
        notificationEvent.setTopic("order-notification-topic");
        notificationEvent.setPayload(jsonUtil.toJson(orderNotification));
        outBoxEventRepository.save(notificationEvent);

        var reserveItems = savedEntityOrder.getOrderItemEntities().stream()
                .map(i -> new OrderItemDto(i.getProductId(), i.getProductName(), i.getPrice(), i.getQuantity()))
                .toList();
        ReserveStockCommand reserveCmd = new ReserveStockCommand(savedEntityOrder.getId(), userId, savedEntityOrder.getStoreId(), reserveItems);

        OutBoxEventEntity reserveEvent = new OutBoxEventEntity();
        reserveEvent.setOrderId(savedEntityOrder.getId());
        reserveEvent.setTopic("warehouse-reserve-topic");
        reserveEvent.setPayload(jsonUtil.toJson(reserveCmd));
        outBoxEventRepository.save(reserveEvent);

        logger.info("OutBoxEvents saved for orderId={}", savedEntityOrder.getId());

        return mapper.toModel(savedEntityOrder);
    }

    @Transactional
    public Order updateOrder(UUID userId, UUID id, Order order) {
        OrderEntity existingOrderEntity = orderRepository.findByUserIdAndId(userId, id).orElseThrow(
                () ->  new EntityNotFoundException("Order not found")
        );

        OrderEntity newOrderEnitity = mapper.toEntity(order);

        existingOrderEntity.getOrderItemEntities().clear();;
        existingOrderEntity.getOrderItemEntities().addAll(newOrderEnitity.getOrderItemEntities());
        existingOrderEntity.setStatus(NotificationType.UPDATED);

        logger.info("Updating order: {}", existingOrderEntity);

        OrderEntity updatedOrderEntity = orderRepository.save(existingOrderEntity);

        logger.info("Updated order: {}", updatedOrderEntity);

        OrderNotification orderNotification = notificationMapper.toNotification(
                userId,
                updatedOrderEntity, NotificationType.UPDATED);

        OutBoxEventEntity notificationEvent = new OutBoxEventEntity();
        notificationEvent.setOrderId(updatedOrderEntity.getId());
        notificationEvent.setTopic("order-notification-topic");
        notificationEvent.setPayload(jsonUtil.toJson(orderNotification));
        outBoxEventRepository.save(notificationEvent);

        var reserveItems = updatedOrderEntity.getOrderItemEntities().stream()
                .map(i -> new OrderItemDto(i.getProductId(), i.getProductName(), i.getPrice(), i.getQuantity()))
                .toList();
        ReserveStockCommand reserveCmd = new ReserveStockCommand(updatedOrderEntity.getId(), userId, updatedOrderEntity.getStoreId(), reserveItems);

        OutBoxEventEntity reserveEvent = new OutBoxEventEntity();
        reserveEvent.setOrderId(updatedOrderEntity.getId());
        reserveEvent.setTopic("warehouse-reserve-topic");
        reserveEvent.setPayload(jsonUtil.toJson(reserveCmd));
        outBoxEventRepository.save(reserveEvent);

        logger.info("OutBoxEvents saved for orderId={}", updatedOrderEntity.getId());

        return mapper.toModel(updatedOrderEntity);

    }

    @Transactional
    public Order updateOrderByAdmin(UUID userId, Order order) {
        OrderEntity existingOrderEntity = orderRepository.findByUserIdAndId(userId, order.id()).orElseThrow(
                () ->  new EntityNotFoundException("Order not found")
        );

        OrderEntity newOrderEnitity = mapper.toEntity(order);

        existingOrderEntity.setUserId(newOrderEnitity.getUserId());
        existingOrderEntity.setStatus(NotificationType.UPDATED);
        existingOrderEntity.getOrderItemEntities().clear();;
        existingOrderEntity.getOrderItemEntities().addAll(newOrderEnitity.getOrderItemEntities());

        logger.info("Updating order: {}", existingOrderEntity);

        OrderEntity updatedOrderEntity = orderRepository.save(existingOrderEntity);

        logger.info("Updated order: {}", updatedOrderEntity);

        return mapper.toModel(updatedOrderEntity);
    }

    @Transactional(readOnly = true)
    public Order getOrderById(UUID userId, UUID orderId) {
        OrderEntity orderEntity = orderRepository.findByUserIdAndId(userId, orderId).orElseThrow(
                () ->  new EntityNotFoundException("Order not found")
        );

        return mapper.toModel(orderEntity);
    }

    @Transactional
    public void deleteOrder(UUID userId, UUID orderId) {
        Optional.of(orderRepository.hideByUserIdAndId(userId, orderId))
                .filter(count -> count > 0)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }
}
