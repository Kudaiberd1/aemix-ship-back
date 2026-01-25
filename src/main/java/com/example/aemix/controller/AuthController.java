package com.example.aemix.controller;

import com.example.aemix.dto.requests.AuthRequest;
import com.example.aemix.dto.responses.AuthResponse;
import com.example.aemix.service.AuthService;
import com.example.aemix.service.EmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.version}/auth")
@Tag(name = "Auth", description = "Регистрация и вход в систему")
public class AuthController {
    private final AuthService authService;
    private final EmailVerificationService verificationService;

    @Operation(
            summary = "Регистрация",
            description = "Создает новый аккаунт пользователя и возвращает подтверждающее сообщение"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Пользователь с таким именем уже существует или данные невалидны", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(
            summary = "Авторизация (Login)",
            description = "Проверяет учетные данные и возвращает JWT токен для доступа к защищенным ресурсам"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Авторизация успешна",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Неверный логин или пароль", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        verificationService.verify(token);
        return ResponseEntity.ok("Email verified ✅");
    }
}