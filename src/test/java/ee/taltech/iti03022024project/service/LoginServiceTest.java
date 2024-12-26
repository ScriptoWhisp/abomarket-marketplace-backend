package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.domain.RoleEntity;
import ee.taltech.iti03022024project.domain.UserEntity;
import ee.taltech.iti03022024project.exception.LoginException;
import ee.taltech.iti03022024project.repository.UsersRepository;
import ee.taltech.iti03022024project.security.LoginRequestDto;
import ee.taltech.iti03022024project.security.LoginResponseDto;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginService loginService;

    private SecretKey secretKey;

    private LoginRequestDto loginRequestDto;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        secretKey = Jwts.SIG.HS256.key().build();
        loginService = new LoginService(secretKey, usersRepository, null, passwordEncoder);

        loginRequestDto = new LoginRequestDto("test@example.com", "password123");
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleId(1);
        roleEntity.setRoleName("ROLE_USER");

        userEntity = new UserEntity();
        userEntity.setUserId(1);
        userEntity.setEmail("test@example.com");
        userEntity.setPassword("encodedPassword");
        userEntity.setRole(roleEntity);
    }

    // ---------------------------------------------------------------------------------------------
    // login
    // ---------------------------------------------------------------------------------------------
    @Test
    void login_ValidCredentials_ReturnsToken() {
        // given
        when(usersRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(loginRequestDto.getPassword(), userEntity.getPassword())).thenReturn(true);

        // when
        LoginResponseDto response = loginService.login(loginRequestDto);

        // then
        assertNotNull(response);
        assertNotNull(response.getJwtToken());
        verify(usersRepository, times(1)).findByEmail(loginRequestDto.getEmail());
        verify(passwordEncoder, times(1)).matches(loginRequestDto.getPassword(), userEntity.getPassword());
    }

    @Test
    void login_UserNotFound_ThrowsLoginException() {
        // given
        when(usersRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.empty());

        // when & then
        LoginException thrown = assertThrows(
                LoginException.class,
                () -> loginService.login(loginRequestDto)
        );

        assertEquals("User Not Found!", thrown.getMessage());
        verify(usersRepository, times(1)).findByEmail(loginRequestDto.getEmail());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void login_InvalidPassword_ThrowsLoginException() {
        // given
        when(usersRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(loginRequestDto.getPassword(), userEntity.getPassword())).thenReturn(false);

        // when & then
        LoginException thrown = assertThrows(
                LoginException.class,
                () -> loginService.login(loginRequestDto)
        );

        assertEquals("Invalid Password!", thrown.getMessage());
        verify(usersRepository, times(1)).findByEmail(loginRequestDto.getEmail());
        verify(passwordEncoder, times(1)).matches(loginRequestDto.getPassword(), userEntity.getPassword());
    }

    // ---------------------------------------------------------------------------------------------
    // generateToken (indirectly tested through login)
    // ---------------------------------------------------------------------------------------------
    @Test
    void generateToken_ValidUser_ReturnsValidJwtToken() {
        // Test `generateToken` indirectly through `login`.
        when(usersRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(loginRequestDto.getPassword(), userEntity.getPassword())).thenReturn(true);

        // when
        LoginResponseDto response = loginService.login(loginRequestDto);

        // then
        assertNotNull(response);
        String token = response.getJwtToken();
        assertNotNull(token);

        // Verify token structure
        String subject = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
        assertEquals("test@example.com", subject);
    }
}
