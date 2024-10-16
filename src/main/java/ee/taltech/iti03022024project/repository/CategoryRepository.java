package ee.taltech.iti03022024project.repository;

import ee.taltech.iti03022024project.domain.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {
}

