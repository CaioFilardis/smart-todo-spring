package com.projetoprionyx.smart_todo.api.controller;


import com.projetoprionyx.smart_todo.api.dto.auth.AuthResponseDto;
import com.projetoprionyx.smart_todo.api.dto.auth.LoginRequestDto;
import com.projetoprionyx.smart_todo.api.dto.auth.RegisterRequestDto;
import com.projetoprionyx.smart_todo.api.dto.auth.UserProfileDto;
import com.projetoprionyx.smart_todo.api.model.User;
import com.projetoprionyx.smart_todo.api.repository.UserRepository;
import com.projetoprionyx.smart_todo.api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    // Injeção por construtor (melhor prática)
    @Autowired
    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registreUser(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        authService.register(registerRequestDto);
        return new ResponseEntity<>("Usuário Registrado com sucesso", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginUser(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        AuthResponseDto authResponseDto = authService.login(loginRequestDto);
        return ResponseEntity.ok(authResponseDto);
    }

    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        boolean exists = userRepository.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        UserProfileDto dto = new UserProfileDto(user.getFullName(), user.getEmail());
        return ResponseEntity.ok(dto);
    }
}
