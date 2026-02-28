package buloshnaya.orders.kafka.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class OrderKafkaProducer {
    private static final Logger logger = LoggerFactory.getLogger(OrderKafkaProducer.class);

    private final KafkaTemplate<String, OrderNotification> kafkaTemplate;

    @Value("${kafka.topic.order-notification}")
    private String orderNotificationTopic;

    public OrderKafkaProducer(KafkaTemplate<String, OrderNotification> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderNotification(OrderNotification orderNotification) {
        try {
            kafkaTemplate.send(orderNotificationTopic, orderNotification).get(5, TimeUnit.SECONDS);
            logger.info("Sent order notification: {}", orderNotification);
        } catch (ExecutionException | TimeoutException e) {
            throw new RuntimeException("Failed to send order notification to Kafka", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while sending order notification to Kafka", e);
        }
    }
}
