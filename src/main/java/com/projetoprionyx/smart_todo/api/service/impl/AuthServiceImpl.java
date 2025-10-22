package com.projetoprionyx.smart_todo.api.service.impl;

import com.projetoprionyx.smart_todo.api.dto.auth.AuthResponseDto;
import com.projetoprionyx.smart_todo.api.dto.auth.LoginRequestDto;
import com.projetoprionyx.smart_todo.api.dto.auth.RegisterRequestDto;
import com.projetoprionyx.smart_todo.api.model.User;
import com.projetoprionyx.smart_todo.api.repository.UserRepository;
import com.projetoprionyx.smart_todo.api.security.jwt.JwtTokenProvider;
import com.projetoprionyx.smart_todo.api.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public void register(RegisterRequestDto registerRequestDto) {
        if (userRepository.findByEmail(registerRequestDto.getEmail()).isPresent()) {
            throw new RuntimeException("Erro: O e-mail já está em uso!");
        }
        User user = new User();
        // Ajustar para o nome correto do campo na sua entidade User e DTO
        // user.setFullName(registerRequestDto.getFullName());
        user.setEmail(registerRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        userRepository.save(user);
    }

    @Override
    public AuthResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getEmail(),
                        loginRequestDto.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);
        return new AuthResponseDto(jwt);
    }
}