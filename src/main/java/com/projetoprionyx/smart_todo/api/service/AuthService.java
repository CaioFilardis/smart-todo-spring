package com.projetoprionyx.smart_todo.api.service;

import com.projetoprionyx.smart_todo.api.dto.auth.AuthResponseDto;
import com.projetoprionyx.smart_todo.api.dto.auth.LoginRequestDto;
import com.projetoprionyx.smart_todo.api.dto.auth.RegisterRequestDto;
import org.springframework.security.authentication.jaas.LoginExceptionResolver;

public interface AuthService {

    // Registra um novo usuário no sistema.
    void register(RegisterRequestDto registerRequestDto);

    // Autentica um usuário existente e retorna um token JWT.
    AuthResponseDto login(LoginRequestDto loginRequestDto);
}
