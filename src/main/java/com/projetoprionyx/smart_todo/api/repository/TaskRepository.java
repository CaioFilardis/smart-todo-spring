package com.projetoprionyx.smart_todo.api.repository;

import com.projetoprionyx.smart_todo.api.model.Task;
import com.projetoprionyx.smart_todo.api.model.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Busca todas as tarefas associadas a um ID de usuário específico.
     * Isso nos permitirá, por exemplo, exibir a lista de tarefas de um usuário logado.
     * A query gerada será: "SELECT t FROM Task t WHERE t.user.id = :userId"
     *
     *.
     */
    List<Task> findByUserId(Long userId); // por ser a parte 'muitos' do relacionamento

    List<Task> findByStatus(TaskStatus status); // buscar por status
}
