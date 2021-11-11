package com.krylov.tasktracker.tasktracker_rest_web_service.service;

import com.krylov.tasktracker.tasktracker_rest_web_service.dto.task.TaskDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.TaskEntity;

import java.util.List;
import java.util.Optional;

public interface TaskService extends BaseEntityService<TaskEntity, TaskDto> {

    Optional<TaskEntity> saveOrUpdate(TaskDto taskInfo);

    Optional<List<TaskEntity>> findAllProjectTasks(Long projectId);

    Optional<List<TaskEntity>> findAllUserTasks(Long userId);

    Optional<List<TaskEntity>> findAllProjectUserTasks(Long projectId, Long userId);

}
