package br.com.eventpass.api.controller;

import br.com.eventpass.application.dto.request.*;
import br.com.eventpass.application.dto.response.AuthResponse;
import br.com.eventpass.application.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/cadastro/iniciar")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void iniciarCadastro(@Valid @RequestBody CadastroRequest req) {
        authService.iniciarCadastro(req);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/verificar-otp")
    public ResponseEntity<AuthResponse> verificarOtp(@Valid @RequestBody VerificarOtpRequest req) {
        return ResponseEntity.ok(authService.verificarOtp(req));
    }
}
