package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.security.LoginResponseDto;
import ee.taltech.iti03022024project.security.LoginRequestDto;
import ee.taltech.iti03022024project.service.LoginService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/api/public/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto request) {
        return loginService.login(request);
    }
}
