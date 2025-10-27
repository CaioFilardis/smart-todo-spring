package com.projetoprionyx.smart_todo.api.controller;

import com.projetoprionyx.smart_todo.api.repository.UserRepository;
import com.projetoprionyx.smart_todo.api.service.AuthService;
import com.projetoprionyx.smart_todo.api.service.impl.AuthServiceImpl;

public class AuthControllerBuilder {
    private AuthService authService;
    private UserRepository userRepository;

    public AuthControllerBuilder setAuthService(AuthService authService) {
        this.authService = authService;
        return this;
    }

    public AuthControllerBuilder setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
        return this;
    }

    public AuthController createAuthController() {
        return new AuthController(authService, userRepository);
    }
}