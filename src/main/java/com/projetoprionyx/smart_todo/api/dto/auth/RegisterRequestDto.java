package com.projetoprionyx.smart_todo.api.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;


@Getter
@Setter
public class RegisterRequestDto {

    @NotBlank(message = "O nome completo não pode estar em branco.")
    @Size(min = 3, message = "O nome completo deve ter no mínimo 3 caracteres.")
    private String fullName;

    @NotBlank(message = "O e-mail não pode estar em branco.")
    @Email(message = "O formato do e-mail é inválido.")
    private String email;

    @NotBlank(message = "A senha não pode estar em branco.")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
    private String password;
}
