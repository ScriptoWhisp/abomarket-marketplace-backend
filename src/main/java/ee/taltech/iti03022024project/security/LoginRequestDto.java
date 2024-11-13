package ee.taltech.iti03022024project.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor @Data
public class LoginRequestDto {
    private String email;
    private String password;
}
