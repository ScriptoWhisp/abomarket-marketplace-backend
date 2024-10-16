package ee.taltech.iti03022024project.repository;

import ee.taltech.iti03022024project.domain.StatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<StatusEntity, Integer> {
}
