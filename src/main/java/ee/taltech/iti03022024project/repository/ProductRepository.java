package ee.taltech.iti03022024project.repository;

import ee.taltech.iti03022024project.domain.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {
}
