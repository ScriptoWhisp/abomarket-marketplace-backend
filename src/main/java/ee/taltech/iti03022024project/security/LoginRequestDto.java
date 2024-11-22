package ee.taltech.iti03022024project.security;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(name = "LoginRequest", description = "DTO for login request")
@AllArgsConstructor @Data
public class LoginRequestDto {
    @Schema(description = "Email of the user", example = "test@gmail.com")
    private String email;
    @Schema(description = "Password of the user", example = "password")
    private String password;
}
