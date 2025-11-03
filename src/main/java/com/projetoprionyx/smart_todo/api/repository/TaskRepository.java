package com.projetoprionyx.smart_todo.api.repository;

import com.projetoprionyx.smart_todo.api.model.Task;
import com.projetoprionyx.smart_todo.api.model.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserId(Long userId); // por ser a parte 'muitos' do relacionamento
    List<Task> findByUserIdAndStatus(Long userId, TaskStatus status); // buscar por status

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND " +
            "(LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Task> searchByTitleOrDescriptionForUser(
            @Param("userId") Long userId,
            @Param("searchTerm") String searchTerm
    );

}
