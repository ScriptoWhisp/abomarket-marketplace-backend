package ee.taltech.iti03022024project.security;

import ee.taltech.iti03022024project.exception.BadTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final SecretKey key;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        try {
        Optional<String> token = getToken(request);
            if (token.isPresent()) {
                Claims tokenBody = parseToken(token.get());
                SecurityContext context = SecurityContextHolder.getContext();
                context.setAuthentication(buildAuthToken(tokenBody));
            }
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature: {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT signature");
            return;
        }

        chain.doFilter(request, response);
    }

    private Optional<String> getToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> header.substring(7));

    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Authentication buildAuthToken(Claims tokenBody) {
        List<?> roles = (List<?>) tokenBody.get("roles");
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> {
                    if (role instanceof LinkedHashMap) {
                        return new SimpleGrantedAuthority((String) ((LinkedHashMap<?, ?>) role).get("authority"));
                    } else if (role instanceof String roleString) {
                        return new SimpleGrantedAuthority(roleString);
                    } else {
                        throw new IllegalArgumentException("Unexpected role format: " + role);
                    }
                })
                .toList();
        return new UsernamePasswordAuthenticationToken(
                tokenBody.getSubject(),
                null,
                authorities
        );
    }
}
