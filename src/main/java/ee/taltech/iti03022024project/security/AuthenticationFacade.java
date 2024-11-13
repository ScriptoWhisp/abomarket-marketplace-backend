package ee.taltech.iti03022024project.security;

import ee.taltech.iti03022024project.domain.UserEntity;
import ee.taltech.iti03022024project.repository.UsersRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class AuthenticationFacade implements IAuthenticationFacade {

    private final UsersRepository usersRepository;

    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public UserEntity getAuthenticatedUser() {
        return usersRepository.findByEmail(getAuthentication().getName()).orElse(null);
    }

    @Override
    public Integer getAuthenticatedUserId() {
        return Optional.ofNullable(getAuthenticatedUser())
                .map(UserEntity::getUserId)
                .orElse(null);
    }
}