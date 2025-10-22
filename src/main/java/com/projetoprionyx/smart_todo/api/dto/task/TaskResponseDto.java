package com.projetoprionyx.smart_todo.api.dto.task;

import com.projetoprionyx.smart_todo.api.model.enums.TaskPriority;
import com.projetoprionyx.smart_todo.api.model.enums.TaskStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
public class TaskResponseDto {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // Nota: Não incluímos o objeto 'User' completo para evitar expor dados
}
