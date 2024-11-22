package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.security.LoginResponseDto;
import ee.taltech.iti03022024project.security.LoginRequestDto;
import ee.taltech.iti03022024project.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@Tag(name = "Login", description = "Operations related to logging in")
public class LoginController {

    private final LoginService loginService;

    @Operation(summary = "Login", description = "Logs in the user and returns a LoginResponseDTO with token.")
    @ApiResponse(responseCode = "200", description = "User logged in successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDto.class)))
    @ApiResponse(responseCode = "401", description = "User with this does not exist in database or password is invalid.", content = @Content())
    @PostMapping("/api/public/login")
    public LoginResponseDto login(@Valid @RequestBody LoginRequestDto request) {
        log.info("Received login request: {}", request.getEmail());
        return loginService.login(request);
    }
}
