package com.projetoprionyx.smart_todo.api.controller;

import com.projetoprionyx.smart_todo.api.dto.auth.AuthResponseDto;
import com.projetoprionyx.smart_todo.api.dto.auth.LoginRequestDto;
import com.projetoprionyx.smart_todo.api.dto.auth.RegisterRequestDto;
import com.projetoprionyx.smart_todo.api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService; // indica injeção de dependências

    // Endpoint
    @PostMapping("/register")
    public ResponseEntity<String> registreUser(@Valid @RequestBody RegisterRequestDto registerRequestDto) {

        // 1. realizar o registro
        authService.register(registerRequestDto);

        // 2. retornar a resposta com status
        return new ResponseEntity<>("Usuário Registrado com sucesso", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginUser(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        AuthResponseDto authResponseDto = authService.login(loginRequestDto);
        return ResponseEntity.ok(authResponseDto);
    }
}
