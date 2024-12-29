package ee.taltech.iti03022024project.repository;

import ee.taltech.iti03022024project.domain.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Integer>, JpaSpecificationExecutor<OrderItemEntity> {
}
