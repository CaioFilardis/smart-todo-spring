package com.projetoprionyx.smart_todo.api.dto.auth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {

    private String accessToken;
    private String tokenType = "Bearer";
    private String message;

    public AuthResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }
}
