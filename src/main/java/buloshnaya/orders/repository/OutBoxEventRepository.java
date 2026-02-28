package buloshnaya.orders.repository;

import buloshnaya.orders.entity.OutBoxEventEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutBoxEventRepository extends JpaRepository<OutBoxEventEntity, Long> {

   List<OutBoxEventEntity> findByPublishedFalseAndRetryCountLessThanOrderByCreatedAtAsc(int maxRetryCount, Pageable pageable);
}
