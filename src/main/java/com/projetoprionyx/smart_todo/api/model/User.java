package com.projetoprionyx.smart_todo.api.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Data // indica a geração de getters e setters / toString / equals e hashcode pelo Lombook
@Entity // indica que é uma entidade JPA
@Table(name = "users") // indica que renomeia a entidade mapeia para a tabela users
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // indica a configuração estratégica da chave primária
    private Long id;

    @Column(name = "nome", nullable = false) // mapeia para o nome da coluna e marca como não-nula
    private String nome;

    @Column(name = "email", nullable = false, unique = false) // indica unicidade e não-nulidade a nível de JPA
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "criadoEm", nullable = false)
    private OffsetDateTime criadoEm;

    @Column(name = "atualizadoEm", nullable = false, updatable = false) // indica proteção do campo há atualizações
    private OffsetDateTime atualizadoEm;
}
