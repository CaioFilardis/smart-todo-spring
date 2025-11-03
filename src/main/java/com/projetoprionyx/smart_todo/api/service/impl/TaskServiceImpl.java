package com.projetoprionyx.smart_todo.api.service.impl;

import com.projetoprionyx.smart_todo.api.dto.ai.AIPredictionDto;
import com.projetoprionyx.smart_todo.api.dto.task.TaskRequestDto;
import com.projetoprionyx.smart_todo.api.dto.task.TaskResponseDto;
import com.projetoprionyx.smart_todo.api.model.Task;
import com.projetoprionyx.smart_todo.api.model.User;
import com.projetoprionyx.smart_todo.api.model.enums.TaskStatus;
import com.projetoprionyx.smart_todo.api.repository.TaskRepository;
import com.projetoprionyx.smart_todo.api.repository.UserRepository;
import com.projetoprionyx.smart_todo.api.service.AIService;
import com.projetoprionyx.smart_todo.api.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AIService aiService;

    @Override
    @Transactional
    public TaskResponseDto createTask(TaskRequestDto taskRequestDto) {
        // 1. Obter o usuário autenticado.
        User currentUser = getCurrentUser();

        // 2. Chamar o AIService para obter as predições.
        AIPredictionDto prediction = aiService.getTaskPrediction(
                taskRequestDto.getTitle(),
                taskRequestDto.getDescription()
        );

        // 3. Criar e popular a nova entidade Task.
        Task newTask = new Task();
        newTask.setUser(currentUser);
        newTask.setTitle(taskRequestDto.getTitle());
        newTask.setDescription(taskRequestDto.getDescription());
        newTask.setStatus(TaskStatus.PENDING); // Status inicial padrão

        // Popula com os dados da IA
        if (prediction != null) {
            newTask.setPriority(prediction.getPriority());
            newTask.setDueDate(prediction.getDueDate());
        }

        // 4. Salvar no banco.
        Task savedTask = taskRepository.save(newTask);

        // 5. Converter a entidade para DTO e retornar.
        return convertToDto(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDto> findAllTasksForCurrentUser() {
        User currentUser = getCurrentUser();
        return taskRepository.findByUserId(currentUser.getId()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDto findTaskById(Long taskId) {
        User currentUser = getCurrentUser();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada com id: " + taskId));

        // Verificação de posse
        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Acesso negado. A tarefa não pertence a este usuário.");
        }
        return convertToDto(task);
    }

    @Override
    public List<TaskResponseDto> findTaskByStatus(TaskStatus status) {
        User currentUser = getCurrentUser();
        // Você precisa criar o método findByUserIdAndStatus no seu TaskRepository
        return taskRepository.findByUserIdAndStatus(currentUser.getId(), status).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Task> listarPorStatus(Long taskId, TaskStatus status) {
        return taskRepository.findByUserIdAndStatus(taskId, status);
    }


    @Override
    @Transactional
    public TaskResponseDto updateTaskStatus(Long taskId, TaskStatus newStatus) {
        User currentUser = getCurrentUser();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada com id: " + taskId));

        // Verificação de posse
        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Acesso negado. A tarefa não pertence a este usuário.");
        }

        task.setStatus(newStatus);
        Task updatedTask = taskRepository.save(task);
        return convertToDto(updatedTask);
    }


    public TaskResponseDto updateTask(Long taskId, TaskRequestDto dto) {
        User currentUser = getCurrentUser();
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException(("Tarefa não encontrada com id: " + taskId)));
        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Acesso negado. A tarefa não pertence a este usuario");
        }
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
        Task updated = taskRepository.save(task);
        return convertToDto(updated);
    }



    @Override
    @Transactional
    public void deleteTask(Long taskId) {
        User currentUser = getCurrentUser();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada com id: " + taskId));

        // Verificação de posse
        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Acesso negado. A tarefa não pertence a este usuário.");
        }
        taskRepository.delete(task);
    }

    @Override
    public List<TaskResponseDto> searchTaskByText(String text) {
        User currentUser = getCurrentUser();
        // Você precisa criar o método customizado no Repository
        return taskRepository.searchByTitleOrDescriptionForUser(currentUser.getId(), text).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    /**
     * Método auxiliar para obter o usuário atualmente autenticado no sistema.
     */
    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado no contexto de segurança."));
    }

    /**
     * Método auxiliar para converter uma entidade Task em um TaskResponseDto.
     */
    private TaskResponseDto convertToDto(Task task) {
        TaskResponseDto dto = new TaskResponseDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setDueDate(task.getDueDate());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        return dto;
    }
}
