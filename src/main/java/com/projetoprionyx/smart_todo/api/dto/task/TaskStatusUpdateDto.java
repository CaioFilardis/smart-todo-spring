package com.projetoprionyx.smart_todo.api.dto.task;

import com.projetoprionyx.smart_todo.api.model.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStatusUpdateDto {

    @NotNull(message = "O novo status não pode ser nulo.")
    private TaskStatus status;
}
