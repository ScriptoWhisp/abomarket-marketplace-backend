package ee.taltech.iti03022024project.repository;

import ee.taltech.iti03022024project.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<UserEntity, Integer> {
}
