package buloshnaya.orders.scheduler;

import buloshnaya.orders.entity.OutBoxEventEntity;
import buloshnaya.orders.kafka.dto.OrderNotification;
import buloshnaya.orders.repository.OutBoxEventRepository;
import buloshnaya.orders.service.OutboxEventService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class OutBoxScheduler {

    private static final Logger logger = LoggerFactory.getLogger(OutBoxScheduler.class);

    private final OutBoxEventRepository outBoxEventRepository;
    private final OutboxEventService outboxEventService;

    @Value("${outbox.max-retry-count}")
    private Integer maxRetryCount;


    @Value("${outbox.batch-size}")
    private Integer batchSize;


    @Scheduled(fixedDelay = 1000)
    public void processOutBoxEvents() {
        List<OutBoxEventEntity> events = outBoxEventRepository
                .findByPublishedFalseAndRetryCountLessThanOrderByCreatedAtAsc(
                        maxRetryCount, Pageable.ofSize(batchSize));

        if (events.isEmpty()) return;


        List<CompletableFuture<SendResult<String, OrderNotification>>> futures = events.stream()
                .map(outboxEventService::publishEvent)
                .toList();


        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .orTimeout(30, TimeUnit.SECONDS)
                .exceptionally(ex -> null)
                .join();


        for (int i = 0 ; i < events.size(); i++) {
            OutBoxEventEntity event = events.get(i);
            CompletableFuture<?> future = futures.get(i);

            try {
                future.getNow(null);
                event.setPublished(true);
            } catch (Exception e) {
                event.setRetryCount(event.getRetryCount() + 1);
                event.setErrorMessage(e.getMessage());
                logger.error("Failed to process outbox event id={}: {}", event.getId(), e.getMessage());

            }
            outBoxEventRepository.save(event);

        }
    }
}
