package com.projetoprionyx.smart_todo.api.model;

import com.projetoprionyx.smart_todo.api.model.enums.TaksPriority;
import com.projetoprionyx.smart_todo.api.model.enums.TasksStatus;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // -- Relacionamento: Muitas tarefas para um usuário ---
    @ManyToOne(fetch = FetchType.LAZY) // indica melhor prática para performar
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    // ------------------------------------------------------

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT") // indica tipo de coluna para campos longos
    private String descricao;

    @Enumerated(EnumType.STRING) // salva um 'Enum' como String
    @Column(nullable = false)
    private TasksStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaksPriority priority;

    @Column(name = "criadoEm", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    @Column(name = "atualizadoEm", nullable = false)
    private OffsetDateTime autalizadoEm;

    // ---- métodos
    protected void criado() {
        this.criadoEm = OffsetDateTime.now();
        this.autalizadoEm = OffsetDateTime.now();
    }

    protected void atualizado() {
        this.autalizadoEm = OffsetDateTime.now();
    }

}
