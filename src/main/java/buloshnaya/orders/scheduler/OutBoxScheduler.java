package buloshnaya.orders.scheduler;

import buloshnaya.orders.entity.OutBoxEventEntity;
import buloshnaya.orders.repository.OutBoxEventRepository;
import buloshnaya.orders.service.OutboxEventService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutBoxScheduler {

    private static final Logger logger = LoggerFactory.getLogger(OutBoxScheduler.class);

    private final OutBoxEventRepository outBoxEventRepository;
    private final OutboxEventService outboxEventService;

    @Value("${outbox.max-retry-count}")
    private Integer maxRetryCount;


    @Scheduled(fixedDelay = 5000)
    public void processOutBoxEvents() {
        Pageable pageable = Pageable.ofSize(5);


        List<OutBoxEventEntity> outBoxEventEntityList = outBoxEventRepository.findByPublishedFalseAndRetryCountLessThanOrderByCreatedAtAsc(maxRetryCount, pageable);

        for (OutBoxEventEntity outBoxEventEntity : outBoxEventEntityList) {

            try {
                outboxEventService.publishEvent(outBoxEventEntity);
                outBoxEventEntity.setPublished(true);

            } catch (Exception e) {
                outBoxEventEntity.setRetryCount(outBoxEventEntity.getRetryCount() + 1);
                outBoxEventEntity.setErrorMessage(e.getMessage());
                logger.error("Failed to process outbox event id={}: {}",
                        outBoxEventEntity.getId(), e.getMessage());

            }

            outBoxEventRepository.save(outBoxEventEntity);
        }
    }
}
