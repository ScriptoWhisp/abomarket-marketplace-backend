package ee.taltech.iti03022024project.security;

import ee.taltech.iti03022024project.repository.UsersRepository;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;


@EnableWebSecurity
@EnableMethodSecurity
@Configuration
@AllArgsConstructor
public class SecurityConfiguration {

    public static final String API_CATEGORIES = "/api/categories/";
    public static final String API_STATUSES = "/api/statuses/";
    public static final String ADMIN = "ADMIN";
    private final UsersRepository usersRepository;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailService(usersRepository);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .exceptionHandling(handling -> {
                    handling.authenticationEntryPoint((request, response, authException) -> {
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        response.getWriter().write("Unauthorized");
                    });
                    handling.accessDeniedHandler((request, response, accessDeniedException) -> {
                        response.setStatus(HttpStatus.FORBIDDEN.value());
                        response.getWriter().write("Unauthorized");
                    });
                })

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "swagger-ui/*", "/v3/api-docs", "v3/api-docs/swagger-config").permitAll()
                        // permit to /api/users/id for all
                        .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/public/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, API_CATEGORIES).hasRole(ADMIN)
                        .requestMatchers(HttpMethod.POST, API_CATEGORIES).hasRole(ADMIN)
                        .requestMatchers(HttpMethod.PATCH, API_CATEGORIES).hasRole(ADMIN)
                        .requestMatchers(HttpMethod.DELETE, API_STATUSES).hasRole(ADMIN)
                        .requestMatchers(HttpMethod.POST, API_STATUSES).hasRole(ADMIN)
                        .requestMatchers(HttpMethod.PATCH, API_STATUSES).hasRole(ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/users").hasRole(ADMIN)
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtRequestFilter(key()), UsernamePasswordAuthenticationFilter.class)
                .build();


    }

    @Bean
    public SecretKey key() {
        return Jwts.SIG.HS256.key().build();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter() {
        return new JwtRequestFilter(key());
    }

}
