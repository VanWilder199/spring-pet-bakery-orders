package buloshnaya.orders.service;

import buloshnaya.orders.entity.OrderEntity;
import buloshnaya.orders.entity.OutBoxEventEntity;
import buloshnaya.orders.filter.SearchFilter;
import buloshnaya.orders.kafka.dto.NotificationType;
import buloshnaya.orders.kafka.dto.OrderNotification;
import buloshnaya.orders.mapper.OrderMapper;
import buloshnaya.orders.mapper.OrderNotificationMapper;
import buloshnaya.orders.model.Order;
import buloshnaya.orders.repository.OrderRepository;
import buloshnaya.orders.repository.OutBoxEventRepository;
import buloshnaya.orders.security.UserPrincipal;
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
    public Page<Order> searchOrderByFilter(UserPrincipal userPrincipal, SearchFilter filter) {
         Pageable pageable = Pageable.ofSize(filter.size()).withPage(filter.page());

         logger.info("Searching orders with filter: {}", filter);

         // TODO fix email (get from token)
        return orderRepository.searchAllByFilter(userPrincipal.id(), pageable, filter.status())
                .map(m -> mapper.toModel(m, null));
    }

    @Transactional(readOnly = true)
    public Page<Order> searchOrderByFilterForAdmin(UUID userId, SearchFilter filter) {
        Pageable pageable = Pageable.ofSize(filter.size()).withPage(filter.page());

        logger.info("Searching orders with filter by Admin: {}", filter);

        // TODO fix email (get from token)
        return orderRepository.searchAllByFilter(userId, pageable, filter.status())
                .map(m -> mapper.toModel(m, null));
    }

    @Transactional
    public Order createOrder(UserPrincipal userPrincipal, Order order) {
        OrderEntity mapperEntity = mapper.toEntity(order);
        mapperEntity.setUserId(userPrincipal.id());
        mapperEntity.setStatus(NotificationType.CONFIRMED);

        logger.info("Creating order: {}", mapperEntity);

        OrderEntity savedEntityOrder = orderRepository.save(mapperEntity);

        logger.info("savedEntityOrder order: {}", savedEntityOrder.toString());

        OrderNotification orderNotification = notificationMapper.toNotification(
                userPrincipal.id(),
                savedEntityOrder, order, NotificationType.CONFIRMED);

        OutBoxEventEntity outBoxEventEntity = new OutBoxEventEntity();
        outBoxEventEntity.setOrderId(savedEntityOrder.getId());
        outBoxEventEntity.setPayload(jsonUtil.toJson(orderNotification));

        outBoxEventRepository.save(outBoxEventEntity);

        logger.info("OutBoxEventEntity saved: {}", outBoxEventEntity.toString());

        return mapper.toModel(savedEntityOrder, order.email());
    }

    @Transactional
    public Order updateOrder(UserPrincipal userPrincipal, UUID id, Order order) {
        OrderEntity existingOrderEntity = orderRepository.findByUserIdAndId(userPrincipal.id(), id).orElseThrow(
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
                userPrincipal.id(),
                updatedOrderEntity, order, NotificationType.UPDATED);

        OutBoxEventEntity outBoxEventEntity = new OutBoxEventEntity();
        outBoxEventEntity.setOrderId(updatedOrderEntity.getId());
        outBoxEventEntity.setPayload(jsonUtil.toJson(orderNotification));

        outBoxEventRepository.save(outBoxEventEntity);

        logger.info("OutBoxEventEntity saved: {}", outBoxEventEntity.toString());

        return mapper.toModel(updatedOrderEntity, order.email());

    }

    @Transactional
    public Order updateOrderByAdmin(UserPrincipal userPrincipal,UUID userId, Order order) {
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

        return mapper.toModel(updatedOrderEntity, order.email());
    }

    @Transactional(readOnly = true)
    public Order getOrderById(UserPrincipal userPrincipal, UUID id) {
        OrderEntity orderEntity = orderRepository.findByUserIdAndId(userPrincipal.id(), id).orElseThrow(
                () ->  new EntityNotFoundException("Order not found")
        );

        return mapper.toModel(orderEntity, null);
    }

    @Transactional
    public void deleteOrder(UserPrincipal userPrincipal, UUID id) {
        Optional.of(orderRepository.hideByUserIdAndId(userPrincipal.id(), id))
                .filter(count -> count > 0)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }
}
