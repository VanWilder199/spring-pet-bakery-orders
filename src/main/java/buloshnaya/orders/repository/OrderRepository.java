package buloshnaya.orders.repository;

import buloshnaya.orders.entity.OrderEntity;
import buloshnaya.orders.kafka.dto.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {

    @Query("""
            SELECT o FROM OrderEntity o
            WHERE o.status = :status AND o.userId = :userId
            """)
    Page<OrderEntity> searchAllByFilter(
            @Param("userId") UUID userId,
            Pageable pageable,
            @Param("status") NotificationType status
    );
}
