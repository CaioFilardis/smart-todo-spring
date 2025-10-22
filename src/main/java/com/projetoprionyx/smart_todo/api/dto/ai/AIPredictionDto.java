package com.projetoprionyx.smart_todo.api.dto.ai;

import com.projetoprionyx.smart_todo.api.model.enums.TaskPriority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AIPredictionDto {

    private TaskPriority priority;
    private LocalDate dueDate;
}
