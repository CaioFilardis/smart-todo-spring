package com.projetoprionyx.smart_todo.api.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
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

    // --- Mapeamento de relacionamento One-to-many
    @OneToMany(
            mappedBy = "user", // campo da entidade
            cascade = CascadeType.ALL, // propagar para a Tasks
            orphanRemoval = true // deletar Task do banco de removida da lista
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Task> tasks = new ArrayList<>();


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
