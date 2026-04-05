package buloshnaya.orders.kafka.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class OrderKafkaProducer {
    private static final Logger logger = LoggerFactory.getLogger(OrderKafkaProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    public OrderKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<SendResult<String, String>> sendEvent(String topic, UUID orderId, String payload) {
        logger.info("Sending event to topic={}, orderId={}", topic, orderId);
        return kafkaTemplate.send(topic, String.valueOf(orderId), payload);
    }
}
