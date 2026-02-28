package buloshnaya.orders.service;

import buloshnaya.orders.entity.OutBoxEventEntity;
import buloshnaya.orders.kafka.dto.OrderKafkaProducer;
import buloshnaya.orders.kafka.dto.OrderNotification;
import buloshnaya.orders.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxEventService {

    private static final Logger logger = LoggerFactory.getLogger(OutboxEventService.class);


    private final OrderKafkaProducer orderKafkaProducer;
    private final JsonUtil jsonUtil;


    public void publishEvent(OutBoxEventEntity event) {
        logger.info("Processing outbox event id={}, orderId={}", event.getId(), event.getOrderId());

        try {
            orderKafkaProducer.sendOrderNotification(jsonUtil.fromJson(event.getPayload(), OrderNotification.class));

            logger.info("Published outbox event id={}", event.getId());

        } catch (Exception e) {
            logger.error("Failed outbox event id={}, retryCount={}: {}",
                    event.getId(), event.getRetryCount(), e.getMessage());

            throw e;
        }

    }
}
