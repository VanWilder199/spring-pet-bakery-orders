package buloshnaya.orders.config;

import buloshnaya.orders.kafka.dto.OrderNotification;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {



    @Bean
    public NewTopic orderNotificationTopic(
            @Value("${kafka.topic.order-notification}") String topicName,
            @Value("${kafka.topic.order-notification.partitions}") int partitions
    ) {
        return TopicBuilder.name(topicName).partitions(partitions).replicas(1).build();
    }

    @Bean
    public ProducerFactory<String, OrderNotification> producerFactory(
            ObjectMapper objectMapper
    )  {
        Map<String, Object> configProperties = new HashMap<>();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProperties.put(ProducerConfig.LINGER_MS_CONFIG, 50);
        configProperties.put(ProducerConfig.BATCH_SIZE_CONFIG, 32768);

        JsonSerializer<OrderNotification> serializer = new JsonSerializer<>(objectMapper);
        serializer.setAddTypeInfo(false);

        return new DefaultKafkaProducerFactory<>(configProperties, new StringSerializer(), serializer);
    }

    @Bean
    public KafkaTemplate<String, OrderNotification> kafkaTemplate(
            ProducerFactory<String, OrderNotification> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

}