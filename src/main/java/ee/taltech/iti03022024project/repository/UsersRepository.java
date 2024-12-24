package ee.taltech.iti03022024project.repository;

import ee.taltech.iti03022024project.domain.UserEntity;
import ee.taltech.iti03022024project.responses.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByEmail(String email);
    Page<UserEntity> findAllByEmailContaining(String email, Pageable pageable);
}
