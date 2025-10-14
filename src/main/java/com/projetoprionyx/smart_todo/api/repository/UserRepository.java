package com.projetoprionyx.smart_todo.api.repository;

import com.projetoprionyx.smart_todo.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca um usuário pelo seu endereço de e-mail.
     * O Spring Data JPA automaticamente implementa este método baseado em seu nome.
     * A query gerada será algo como: "SELECT u FROM User u WHERE u.email = :email"
     *
     * @param email O e-mail do usuário a ser buscado.
     * @return um Optional contendo o User se encontrado, ou um Optional vazio caso contrário.
     */
    Optional<User> findByEmail(String email);
}
