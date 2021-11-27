package com.krylov.tasktracker.tasktracker_rest_web_service.service;

import com.krylov.tasktracker.tasktracker_rest_web_service.dto.task.TaskDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.task.TaskStatusChangeRequestDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.TaskEntity;

import java.util.List;

public interface TaskService extends BaseEntityService<TaskEntity, TaskDto> {

    TaskEntity saveOrUpdate(TaskDto taskInfo);

    TaskEntity checkAccessAndSaveOrUpdateStatus(Long userId, TaskStatusChangeRequestDto taskStatus);

    List<TaskEntity> findAllProjectTasks(Long projectId);

    List<TaskEntity> findAllUserTasks(Long userId);

    List<TaskEntity> findAllProjectUserTasks(Long projectId, Long userId);

}
