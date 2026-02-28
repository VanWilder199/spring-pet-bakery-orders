package buloshnaya.orders.service;

import buloshnaya.orders.entity.OutBoxEventEntity;
import buloshnaya.orders.kafka.dto.OrderKafkaProducer;
import buloshnaya.orders.kafka.dto.OrderNotification;
import buloshnaya.orders.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class OutboxEventService {

    private static final Logger logger = LoggerFactory.getLogger(OutboxEventService.class);


    private final OrderKafkaProducer orderKafkaProducer;
    private final JsonUtil jsonUtil;


    public CompletableFuture<SendResult<String, OrderNotification>> publishEvent(OutBoxEventEntity event) {
        logger.info("Processing outbox event id={}, orderId={}", event.getId(), event.getOrderId());

        OrderNotification notification = jsonUtil.fromJson(event.getPayload(), OrderNotification.class);

        return orderKafkaProducer.sendOrderNotification(event.getOrderId(),notification);


    }
}
