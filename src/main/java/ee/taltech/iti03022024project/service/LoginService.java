package ee.taltech.iti03022024project.service;


import ee.taltech.iti03022024project.domain.UserEntity;
import ee.taltech.iti03022024project.exception.LoginException;
import ee.taltech.iti03022024project.mapstruct.UserMapper;
import ee.taltech.iti03022024project.repository.UsersRepository;
import ee.taltech.iti03022024project.security.LoginRequestDto;
import ee.taltech.iti03022024project.security.LoginResponseDto;
import io.jsonwebtoken.Jwts;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {

    private final SecretKey key;

    private final UsersRepository usersRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public LoginResponseDto login(LoginRequestDto request) {
        log.info("Attempting to login : {}", request.getEmail());
        UserEntity user = usersRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new LoginException("User Not Found!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new LoginException("Invalid Password!");
        }

        String token = generateToken(user);
        log.info("User {} logged in", request.getEmail());
        return new LoginResponseDto(token);
    }

    private String generateToken(UserEntity user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .claims(Map.of(
                        "userId", user.getUserId(),
                        "roles", user.getAuthorities()
                ))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(key)
                .compact();
    }

}
