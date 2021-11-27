package com.krylov.tasktracker.tasktracker_rest_web_service.repository;

import com.krylov.tasktracker.tasktracker_rest_web_service.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    Optional<TaskEntity> findByName(String name);

    boolean existsById(Long id);

    @Query("FROM TaskEntity WHERE project.id = ?1")
    List<TaskEntity> findAllByProjectId(Long projectId);

    @Query("FROM TaskEntity WHERE user.id = ?1")
    List<TaskEntity> findAllByUserId(Long userId);

    @Query("FROM TaskEntity WHERE project.id = ?1 and user.id = ?2")
    List<TaskEntity> findAllByProjectIdAndUserId(Long projectId, Long userId);

    @Query("FROM TaskEntity WHERE id = ?1 and user.id = ?2")
    Optional<TaskEntity> findByIdAndUserId(Long taskId, Long userId);

}