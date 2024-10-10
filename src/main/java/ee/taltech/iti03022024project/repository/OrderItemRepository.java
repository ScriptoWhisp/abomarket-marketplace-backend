package ee.taltech.iti03022024project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Integer> {
}
