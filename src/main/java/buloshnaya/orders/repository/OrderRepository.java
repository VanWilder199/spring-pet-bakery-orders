package buloshnaya.orders.repository;

import buloshnaya.orders.entity.OrderEntity;
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
            SELECT o from OrderEntity o
            WHERE (:status IS NULL OR o.status = :status)
            """)
    Page<OrderEntity> searchAllByFilter(
            Pageable pageable,
            @Param("status") String status
    );
}
