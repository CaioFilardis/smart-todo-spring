package com.projetoprionyx.smart_todo.api.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para requisições de login.
 * Contém apenas as credenciais necessárias para autenticar um usuário.
 */
@Data
public class LoginRequestDto {

    @NotBlank(message = "O e-mail não pode estar em branco.")
    @Email(message = "O formato do e-mail é inválido.")
    private String email;

    @NotBlank(message = "A senha não pode estar em branco.")
    private String password;
}