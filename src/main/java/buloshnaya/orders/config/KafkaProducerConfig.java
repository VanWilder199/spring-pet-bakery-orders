package buloshnaya.orders.config;

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
    public NewTopic warehouseReserveTopic(
            @Value("${kafka.topic.warehouse-reserve}") String topicName
    ) {
        return TopicBuilder.name(topicName).partitions(4).replicas(1).build();
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProperties = new HashMap<>();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProperties.put(ProducerConfig.LINGER_MS_CONFIG, 50);
        configProperties.put(ProducerConfig.BATCH_SIZE_CONFIG, 32768);

        return new DefaultKafkaProducerFactory<>(configProperties, new StringSerializer(), new StringSerializer());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(
            ProducerFactory<String, String> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

}