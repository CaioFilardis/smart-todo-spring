package com.projetoprionyx.smart_todo.api.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "email", nullable = false, unique = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "criadoEm", nullable = false)
    private OffsetDateTime criadoEm;

    @Column(name = "atualizadoEm", nullable = false, updatable = false)
    private OffsetDateTime atualizadoEm;


    // gerando métodos - Ciclo de vioda JPA

    @PrePersist
    protected void criacao() {
        this.criadoEm = OffsetDateTime.now();
        this.atualizadoEm = OffsetDateTime.now();
    }

    @PreUpdate
    protected void atualizacao() {
        this.atualizadoEm = OffsetDateTime.now();
    }
}
