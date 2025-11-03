package com.projetoprionyx.smart_todo.api.repository;

import com.projetoprionyx.smart_todo.api.model.Task;
import com.projetoprionyx.smart_todo.api.model.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserId(Long userId); // por ser a parte 'muitos' do relacionamento
    List<Task> findByStatus(TaskStatus status); // buscar por status

    List<Task> searchByTitleOrDescription(@Param("userId") Long userId, @Param("text") String text);

}
