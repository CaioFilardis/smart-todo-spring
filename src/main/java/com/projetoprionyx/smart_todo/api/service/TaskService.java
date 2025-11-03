package com.projetoprionyx.smart_todo.api.service;

import com.projetoprionyx.smart_todo.api.dto.task.TaskRequestDto;
import com.projetoprionyx.smart_todo.api.dto.task.TaskResponseDto;
import com.projetoprionyx.smart_todo.api.model.Task;
import com.projetoprionyx.smart_todo.api.model.enums.TaskStatus;

import java.util.List;

public interface TaskService {

    // Cria uma nova tarefa para o usuário atualmente autenticado.
    TaskResponseDto createTask(TaskRequestDto taskRequestDto);

    // Busca todas as tarefas pertencentes ao usuário atualmente autenticado.
    List<TaskResponseDto> findAllTasksForCurrentUser();

    // Busca uma tarefa específica pelo seu ID, verificando se ela pertence ao usuário logado.
    TaskResponseDto findTaskById(Long id);

    TaskResponseDto findTaskByStatus(TaskStatus status);

    // Atualiza o status de uma tarefa existente.
    TaskResponseDto updateTaskStatus(Long taskId, TaskStatus newStatus);

    // Atualizar tarefa
    TaskResponseDto updateTask(Long taskId, TaskRequestDto dto);

    // Deleta uma tarefa, verificando se ela pertence ao usuário logado.
    void deleteTask(Long taskId);

    // Buscar tarefa pelo título ou descrição
    List<TaskResponseDto> searchTaskByText(String text);

}
