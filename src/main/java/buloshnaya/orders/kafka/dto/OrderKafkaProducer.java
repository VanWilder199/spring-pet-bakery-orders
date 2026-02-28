package buloshnaya.orders.kafka.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class OrderKafkaProducer {
    private static final Logger logger = LoggerFactory.getLogger(OrderKafkaProducer.class);

    private final KafkaTemplate<String, OrderNotification> kafkaTemplate;

    @Value("${kafka.topic.order-notification}")
    private String orderNotificationTopic;

    public OrderKafkaProducer(KafkaTemplate<String, OrderNotification> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<SendResult<String, OrderNotification>> sendOrderNotification(String orderId, OrderNotification orderNotification) {

            logger.info("Sent order notification: {}", orderNotification);
       return kafkaTemplate.send(orderNotificationTopic, orderId, orderNotification);
    }
}
