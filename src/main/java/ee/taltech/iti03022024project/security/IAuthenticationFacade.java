package ee.taltech.iti03022024project.security;

import ee.taltech.iti03022024project.domain.UserEntity;
import org.springframework.security.core.Authentication;

public interface IAuthenticationFacade {
    Authentication getAuthentication();
    UserEntity getAuthenticatedUser();
    Integer getAuthenticatedUserId();
}