package com.projetoprionyx.smart_todo.api.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskRequestDto {

    @NotBlank(message = "O título não pode estar em branco")
    @Size(max = 255, message = "O título não pode exceder 255 caracteres.")
    private String title;

    @Size(max = 4000, message = "A descrição é muito longa.")
    private String description;
}
