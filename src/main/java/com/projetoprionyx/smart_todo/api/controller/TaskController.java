package com.projetoprionyx.smart_todo.api.controller;

import com.projetoprionyx.smart_todo.api.dto.task.TaskRequestDto;
import com.projetoprionyx.smart_todo.api.dto.task.TaskResponseDto;
import com.projetoprionyx.smart_todo.api.dto.task.TaskStatusUpdateDto;
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

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponseDto> updateTaskStatus(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskStatusUpdateDto statusUpdateDto) {
        TaskResponseDto updatedTask = taskService.updateTaskStatus(taskId, statusUpdateDto.getStatus());
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build(); // Retorna HTTP 204 No Content
    }
}
