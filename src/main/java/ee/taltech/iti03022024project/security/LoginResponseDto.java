package ee.taltech.iti03022024project.security;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(name = "LoginResponse", description = "DTO for login response")
@AllArgsConstructor
@Data
public class LoginResponseDto {
    @Schema(description = "JWT token", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0In0.")
    private String JWTToken;
}
