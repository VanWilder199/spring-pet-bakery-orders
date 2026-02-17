package buloshnaya.orders.kafka.dto;

import buloshnaya.orders.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderKafkaProducer {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);


    private final KafkaTemplate kafkaTemplate;

    public OrderKafkaProducer(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderNotification(OrderNotification orderNotification) {
        kafkaTemplate.send("order-notification-topic", orderNotification);

        logger.info("Sent order notification: {}", orderNotification);
    }
}
