package com.projetoprionyx.smart_todo.api.controller;

import com.projetoprionyx.smart_todo.api.dto.task.TaskRequestDto;
import com.projetoprionyx.smart_todo.api.dto.task.TaskResponseDto;
import com.projetoprionyx.smart_todo.api.dto.task.TaskStatusUpdateDto;
import com.projetoprionyx.smart_todo.api.model.enums.TaskStatus; // Import necessário
import com.projetoprionyx.smart_todo.api.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(@Valid @RequestBody TaskRequestDto taskRequestDto) {
        TaskResponseDto createdTask = taskService.createTask(taskRequestDto);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllTasksForCurrentUser() {
        List<TaskResponseDto> tasks = taskService.findAllTasksForCurrentUser();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long taskId) {
        TaskResponseDto task = taskService.findTaskById(taskId);
        return ResponseEntity.ok(task);
    }

    // CORRIGIDO: Adicionado @GetMapping para o endpoint de busca por status
    @GetMapping(params = "status") // Será ativado se o parâmetro 'status' estiver presente
    public ResponseEntity<List<TaskResponseDto>> listarTarefasPorStatus(@RequestParam TaskStatus status) {
        // Você precisa implementar a lógica em TaskServiceImpl para converter para DTO
        List<TaskResponseDto> tasks = taskService.findTaskByStatus(status);
        return ResponseEntity.ok(tasks);
    }

    // CORRIGIDO: Adicionado @GetMapping para o endpoint de busca por texto
    @GetMapping("/search") // Endpoint que o seu frontend estava tentando chamar
    public ResponseEntity<List<TaskResponseDto>> searchTasks(@RequestParam("term") String text) {
        // Você precisa implementar a lógica em TaskServiceImpl para converter para DTO
        List<TaskResponseDto> tasks = taskService.searchTaskByText(text);
        return ResponseEntity.ok(tasks);
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponseDto> updateTaskStatus(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskStatusUpdateDto statusUpdateDto) {
        TaskResponseDto updatedTask = taskService.updateTaskStatus(taskId, statusUpdateDto.getStatus());
        return ResponseEntity.ok(updatedTask);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDto> updateTask(@PathVariable Long taskId, @Valid @RequestBody TaskRequestDto requestDto) {
        TaskResponseDto updatedTask = taskService.updateTask(taskId, requestDto);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build(); // Retorna HTTP 204 No Content
    }
}