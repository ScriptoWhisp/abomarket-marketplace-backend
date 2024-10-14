package ee.taltech.iti03022024project.repository;

import ee.taltech.iti03022024project.domain.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {
}
