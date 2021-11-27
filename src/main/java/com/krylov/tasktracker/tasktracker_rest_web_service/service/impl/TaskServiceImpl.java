package com.krylov.tasktracker.tasktracker_rest_web_service.service.impl;

import com.krylov.tasktracker.tasktracker_rest_web_service.dto.task.TaskDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.task.TaskStatusChangeRequestDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.ProjectEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.TaskEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.UserEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.enums.EntityStatus;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.enums.EntityType;
import com.krylov.tasktracker.tasktracker_rest_web_service.exception.AccessDeniedException;
import com.krylov.tasktracker.tasktracker_rest_web_service.exception.DataBaseUpdateException;
import com.krylov.tasktracker.tasktracker_rest_web_service.exception.InvalidDtoException;
import com.krylov.tasktracker.tasktracker_rest_web_service.exception.NoSuchElementExceptionFactory;
import com.krylov.tasktracker.tasktracker_rest_web_service.repository.ProjectRepository;
import com.krylov.tasktracker.tasktracker_rest_web_service.repository.TaskRepository;
import com.krylov.tasktracker.tasktracker_rest_web_service.repository.UserRepository;
import com.krylov.tasktracker.tasktracker_rest_web_service.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final NoSuchElementExceptionFactory noSuchElementExceptionFactory;


    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository,
                           ProjectRepository projectRepository,
                           UserRepository userRepository,
                           NoSuchElementExceptionFactory noSuchElementExceptionFactory) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.noSuchElementExceptionFactory = noSuchElementExceptionFactory;
    }

    @Override
    @Transactional
    public TaskEntity saveOrUpdate(TaskDto dto) {
        TaskEntity taskEntity = toEntity(dto);
        try {
            TaskEntity result = taskRepository.save(taskEntity);
            return result;
        } catch (Exception e) {
            throw new DataBaseUpdateException("Unable to save Task with name='" + taskEntity.getName() + "'");
        }
    }

    @Override
    @Transactional
    public TaskEntity checkAccessAndSaveOrUpdateStatus(Long userId, TaskStatusChangeRequestDto dto) {
        if (dto == null) throw new InvalidDtoException("DTO is empty");
        if (dto.getId() == null ||
                dto.getStatus() == null) throw new InvalidDtoException("Invalid DTO data");
        TaskEntity taskEntity = taskRepository.findByIdAndUserId(dto.getId(), userId).orElseThrow(
                () -> new AccessDeniedException("User with id='" + userId + "' does not have access to task with id ='" + dto.getId() + "'"));
        taskEntity.setStatus(dto.getStatus());
        try {
            TaskEntity result = taskRepository.save(taskEntity);
            return result;
        } catch (Exception e) {
            throw new DataBaseUpdateException("Unable to save Task with name='" + taskEntity.getName() + "'");
        }
    }

    @Override
    @Transactional
    public List<TaskEntity> getAll() {
        List<TaskEntity> result = taskRepository.findAll();
        return result;
    }

    @Override
    @Transactional
    public TaskEntity findById(Long id) {
        TaskEntity result = taskRepository.findById(id)
                .orElseThrow(() -> noSuchElementExceptionFactory.getNoSuchElementException(EntityType.TASK, "id", id));
        return result;
    }

    @Override
    @Transactional
    public TaskEntity findByName(String name) {
        TaskEntity result = taskRepository.findByName(name).orElseThrow(
                () -> noSuchElementExceptionFactory.getNoSuchElementException(EntityType.TASK, "name", name)
        );
        return result;
    }

    @Override
    @Transactional
    public List<TaskEntity> findAllProjectTasks(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw noSuchElementExceptionFactory.getNoSuchElementException(EntityType.PROJECT, "id", projectId);
        }
        List<TaskEntity> result = taskRepository.findAllByProjectId(projectId);
        return result;
    }

    @Override
    @Transactional
    public List<TaskEntity> findAllUserTasks(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw noSuchElementExceptionFactory.getNoSuchElementException(EntityType.USER, "id", userId);
        }
        List<TaskEntity> result = taskRepository.findAllByUserId(userId);
        return result;
    }

    @Override
    @Transactional
    public List<TaskEntity> findAllProjectUserTasks(Long projectId, Long userId) {
        if (!projectRepository.existsById(projectId)) {
            throw noSuchElementExceptionFactory.getNoSuchElementException(EntityType.PROJECT, "id", projectId);
        }
        if (!userRepository.existsById(userId)) {
            throw noSuchElementExceptionFactory.getNoSuchElementException(EntityType.USER, "id", userId);
        }

        List<TaskEntity> result = taskRepository.findAllByProjectIdAndUserId(projectId, userId);
        return result;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!taskRepository.existsById(id)) {
            throw noSuchElementExceptionFactory.getNoSuchElementException(EntityType.TASK, "id", id);
        }
        try {
            taskRepository.deleteById(id);
        } catch (Exception e) {
            throw new DataBaseUpdateException("Unable to delete Task with id='" + id + "'");
        }
    }

    @Override
    @Transactional
    public boolean existsById(Long id) {
        return taskRepository.existsById(id);
    }

    @Override
    @Transactional
    public TaskEntity toEntity(TaskDto dto) {
        if (dto == null) throw new InvalidDtoException("DTO is empty");
        if (dto.getProjectId() == null ||
                dto.getName() == null ||
                dto.getDescription() == null ||
                dto.getEntityStatus() == null) throw new InvalidDtoException("Invalid DTO data");

        ProjectEntity projectEntity = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> noSuchElementExceptionFactory.getNoSuchElementException(EntityType.PROJECT, "id", dto.getProjectId()));

        TaskEntity result = null;
        if (dto.getId() == null) {
            result = new TaskEntity();
            result.setId(0l);
            result.setCreated(new Date());
        } else {
            Optional<TaskEntity> taskEntityOptional = taskRepository.findById(dto.getId());
            result = taskEntityOptional
                    .orElseThrow(() -> noSuchElementExceptionFactory.getNoSuchElementException(EntityType.TASK, "id", dto.getId()));
        }
        result.setName(dto.getName());
        result.setDescription(dto.getDescription());
        result.setProject(projectEntity);

        if (dto.getUserId() != null) {
            UserEntity userEntity = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> noSuchElementExceptionFactory.getNoSuchElementException(EntityType.USER, "id", dto.getUserId()));
            result.setUser(userEntity);
        }

        result.setStatus(EntityStatus.ACTIVE);
        result.setUpdated(new Date());

        return result;
    }

    @Override
    public TaskDto toDto(TaskEntity entity) {
        TaskDto result = new TaskDto();

        result.setId(entity.getId());
        result.setName(entity.getName());
        result.setDescription(entity.getDescription());
        result.setProjectId(entity.getProject().getId());
        result.setProjectName(entity.getProject().getName());
        if (entity.getUser() != null) {
            result.setUserId(entity.getUser().getId());
            result.setUserName(entity.getUser().getUserName());
        }
        result.setEntityStatus(entity.getStatus());

        return result;
    }

    @Override
    public List<TaskDto> toDtoList(List<TaskEntity> entityList) {
        List<TaskDto> result =
                entityList.stream().map(taskEntity -> toDto(taskEntity)).collect(Collectors.toList());
        return result;
    }
}
