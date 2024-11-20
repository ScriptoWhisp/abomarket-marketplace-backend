package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.security.LoginResponseDto;
import ee.taltech.iti03022024project.security.LoginRequestDto;
import ee.taltech.iti03022024project.service.LoginService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/api/public/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto request) {
        log.info("Received login request: {}", request);
        return loginService.login(request);
    }
}
