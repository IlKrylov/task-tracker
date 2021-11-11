package com.krylov.tasktracker.tasktracker_rest_web_service.service.impl;

import com.krylov.tasktracker.tasktracker_rest_web_service.dto.task.TaskDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.ProjectEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.TaskEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.UserEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.enums.EntityStatus;
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


    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository,
                           ProjectRepository projectRepository,
                           UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Optional<TaskEntity> saveOrUpdate(TaskDto dto) {
        Optional<TaskEntity> entityOptional = toEntity(dto);
        if (entityOptional.isEmpty()) return Optional.empty();

        TaskEntity entity = taskRepository.save(entityOptional.get());

        return Optional.ofNullable(entity);
    }

    @Override
    @Transactional
    public List<TaskEntity> getAll() {
        List<TaskEntity> result = taskRepository.findAll();
        return result;
    }

    @Override
    @Transactional
    public Optional<TaskEntity> findById(Long id) {
        Optional<TaskEntity> result = taskRepository.findById(id);
        return result;
    }

    @Override
    @Transactional
    public Optional<TaskEntity> findByName(String name) {
        return Optional.ofNullable(taskRepository.findByName(name));
    }

    @Override
    @Transactional
    public Optional<List<TaskEntity>> findAllProjectTasks(Long projectId) {
        if (!projectRepository.existsById(projectId)) return Optional.empty();
        List<TaskEntity> result = taskRepository.findAllByProject(projectId);
        return Optional.ofNullable(result);
    }

    @Override
    @Transactional
    public Optional<List<TaskEntity>> findAllUserTasks(Long userId) {
        if (!userRepository.existsById(userId)) return Optional.empty();
        List<TaskEntity> result = taskRepository.findAllByUser(userId);
        return Optional.ofNullable(result);
    }

    @Override
    @Transactional
    public Optional<List<TaskEntity>> findAllProjectUserTasks(Long projectId, Long userId) {
        if (!projectRepository.existsById(projectId)) return Optional.empty();
        if (!userRepository.existsById(userId)) return Optional.empty();

        List<TaskEntity> result = taskRepository.findAllByProjectAndUser(projectId, userId);
        return Optional.ofNullable(result);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    @Transactional
    public boolean existsById(Long id) {
        return taskRepository.existsById(id);
    }

    @Override
    public Optional<TaskEntity> toEntity(TaskDto dto) {
        if (dto == null || dto.getProjectId() == null) return Optional.empty();
        Optional<ProjectEntity> projectOptional = projectRepository.findById(dto.getProjectId());
        if (projectOptional.isEmpty()) return Optional.empty();

        Long id = dto.getId();

        TaskEntity result = null;
        if (id == null) {
            result = new TaskEntity();
            result.setId(0l);
            result.setCreated(new Date());
        } else {
            Optional<TaskEntity> taskEntityOptional = taskRepository.findById(dto.getId());
            if (taskEntityOptional.isEmpty()) return Optional.empty();
            result = taskEntityOptional.get();
        }
        result.setName(dto.getName());
        result.setDescription(dto.getDescription());
        result.setProject(projectOptional.get());

        if (dto.getUserId() != null){
            Optional<UserEntity> userOptional = userRepository.findById(dto.getUserId());
            if (userOptional.isEmpty()) return Optional.empty();
            result.setUser(userOptional.get());
        }

        result.setStatus(EntityStatus.ACTIVE);
        result.setUpdated(new Date());

        return Optional.of(result);
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

        return result;
    }

    @Override
    public List<TaskDto> toDtoList(List<TaskEntity> entityList) {
        List<TaskDto> result =
                entityList.stream().map(taskEntity -> toDto(taskEntity)).collect(Collectors.toList());
        return result;
    }
}
