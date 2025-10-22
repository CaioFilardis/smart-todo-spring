package com.projetoprionyx.smart_todo.api.controller;

import com.projetoprionyx.smart_todo.api.service.AuthService;

public class AuthControllerBuilder {
    private AuthService authService;

    public AuthControllerBuilder setAuthService(AuthService authService) {
        this.authService = authService;
        return this;
    }

    public AuthController createAuthController() {
        return new AuthController(authService);
    }
}