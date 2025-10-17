package com.projetoprionyx.smart_todo.api.security;

import com.projetoprionyx.smart_todo.api.model.User;
import com.projetoprionyx.smart_todo.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true) // indica otimização, apenas leitura
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. buscar nosso usuário pelo e-mail
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(email));

        // 2. converter nossa entidade 'User' para um 'UserDetails', objeto de entendimento do Spring Security
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList() // TODO: Mapear os perfis do usário no futuro
        );
    }
}
