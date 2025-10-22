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
        // 1. Verificar se o e-mail já está em uso.
        if (userRepository.findByEmail(registerRequestDto.getEmail()).isPresent()) {
            throw new RuntimeException("Erro: O e-mail já está em uso!");
        }

        // 2. Criar uma nova instância da entidade User.
        User user = new User();
        user.setFullName(registerRequestDto.getFullName());
        user.setEmail(registerRequestDto.getEmail());

        // 3. CRIPTOGRAFAR A SENHA ANTES DE SALVAR!
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));

        // 4. Salvar o novo usuário no banco de dados.
        userRepository.save(user);
    }

    @Override
    public AuthResponseDto login(LoginRequestDto loginRequestDto) {
        // 1. Criar o objeto de autenticação com as credenciais fornecidas.
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginRequestDto.getEmail(),
                loginRequestDto.getPassword()
        );

        // 2. Usar o AuthenticationManager para validar as credenciais.
        // Ele usará internamente nosso UserDetailsServiceImpl e o PasswordEncoder.
        Authentication authentication = authenticationManager.authenticate(authToken);

        // 3. Se a autenticação for bem-sucedida, definir a autenticação no contexto de segurança.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 4. Gerar o token JWT para a autenticação validada.
        String jwt = jwtTokenProvider.generateToken(authentication);

        // 5. Retornar a resposta com o token.
        return new AuthResponseDto(jwt);
    }
}