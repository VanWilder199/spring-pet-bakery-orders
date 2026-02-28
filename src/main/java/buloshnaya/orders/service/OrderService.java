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
import buloshnaya.orders.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OutBoxEventRepository outBoxEventRepository;
    private final OrderMapper mapper;
    private final OrderNotificationMapper notificationMapper;
    private final JsonUtil jsonUtil;


    public Page<Order> searchOrderByFilter(SearchFilter filter) {
         Pageable pageable = Pageable.ofSize(filter.size()).withPage(filter.page());

         logger.info("Searching orders with filter: {}", filter);

        return orderRepository.searchAllByFilter(pageable, filter.status())
                .map(mapper::toModel);
    }

    @Transactional
    public Order createOrder(Order order) {
        OrderEntity mapperEntity = mapper.toEntity(order);
        mapperEntity.setStatus(NotificationType.CONFIRMED);

        logger.info("Creating order: {}", mapperEntity);

        OrderEntity savedEntityOrder = orderRepository.save(mapperEntity);

        logger.info("savedEntityOrder order: {}", savedEntityOrder.toString());

        OrderNotification orderNotification = notificationMapper.toNotification(
                savedEntityOrder, order, NotificationType.CONFIRMED);

        OutBoxEventEntity outBoxEventEntity = new OutBoxEventEntity();
        outBoxEventEntity.setOrderId(savedEntityOrder.getId());
        outBoxEventEntity.setPayload(jsonUtil.toJson(orderNotification));

        outBoxEventRepository.save(outBoxEventEntity);

        logger.info("OutBoxEventEntity saved: {}", outBoxEventEntity.toString());

        return mapper.toModel(savedEntityOrder);
    }

}
