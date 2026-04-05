package buloshnaya.orders.service;

import buloshnaya.orders.entity.OutBoxEventEntity;
import buloshnaya.orders.kafka.dto.OrderKafkaProducer;
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

    public CompletableFuture<SendResult<String, String>> publishEvent(OutBoxEventEntity event) {
        logger.info("Processing outbox event id={}, orderId={}, topic={}", event.getId(), event.getOrderId(), event.getTopic());
        return orderKafkaProducer.sendEvent(event.getTopic(), event.getOrderId(), event.getPayload());
    }
}
