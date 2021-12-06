package com.krylov.tasktracker.tasktracker_rest_web_service.service.impl;

import com.krylov.tasktracker.tasktracker_rest_web_service.dto.project.ProjectDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.ProjectEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.enums.EntityStatus;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.enums.EntityType;
import com.krylov.tasktracker.tasktracker_rest_web_service.exception.DataBaseUpdateException;
import com.krylov.tasktracker.tasktracker_rest_web_service.exception.InvalidDtoException;
import com.krylov.tasktracker.tasktracker_rest_web_service.exception.NoSuchElementExceptionFactory;
import com.krylov.tasktracker.tasktracker_rest_web_service.repository.ProjectRepository;
import com.krylov.tasktracker.tasktracker_rest_web_service.repository.TaskRepository;
import com.krylov.tasktracker.tasktracker_rest_web_service.repository.UserRepository;
import com.krylov.tasktracker.tasktracker_rest_web_service.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final NoSuchElementExceptionFactory noSuchElementExceptionFactory;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository,
                              UserRepository userRepository,
                              TaskRepository taskRepository,
                              NoSuchElementExceptionFactory noSuchElementExceptionFactory) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.noSuchElementExceptionFactory = noSuchElementExceptionFactory;
    }

    @Override
    @Transactional
    public ProjectEntity saveOrUpdate(ProjectDto dto) {
        ProjectEntity projectEntity = toEntity(dto);
        try {
            ProjectEntity result = projectRepository.save(projectEntity);
            return result;
        } catch (Exception e) {
            throw new DataBaseUpdateException("Unable to save Project with name='" + projectEntity.getName() + "'");
        }
    }

    @Override
    @Transactional
    public List<ProjectEntity> getAll() {
        List<ProjectEntity> result = projectRepository.findAll();
        return result;
    }

    @Override
    @Transactional
    public ProjectEntity findById(Long id) {
        ProjectEntity result = projectRepository.findById(id)
                .orElseThrow(() -> noSuchElementExceptionFactory.getNoSuchElementException(EntityType.PROJECT, "id", id));
        return result;
    }

    @Override
    @Transactional
    public ProjectEntity findByName(String name) {
        ProjectEntity result = projectRepository.findByName(name).orElseThrow(
                () -> noSuchElementExceptionFactory.getNoSuchElementException(EntityType.PROJECT, "name", name));
        return result;
    }

    @Override
    @Transactional
    public List<ProjectEntity> findAllUserProjects(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw noSuchElementExceptionFactory.getNoSuchElementException(EntityType.USER, "id", userId);
        }
        List<ProjectEntity> result = projectRepository.findAllByUserId(userId);
        return result;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        ProjectEntity projectEntity = projectRepository.findById(id)
                .orElseThrow(() -> noSuchElementExceptionFactory.getNoSuchElementException(EntityType.PROJECT, "id", id));

        if (projectEntity.getUsers() != null) {
            projectEntity.getUsers().stream().forEach(userEntity -> userEntity.removeProject(projectEntity, new ArrayList<>()));
        }
        if (projectEntity.getTasks() != null) {
            projectEntity.getTasks().stream().forEach(taskEntity -> taskRepository.deleteById(taskEntity.getId()));
        }

        try {
            projectRepository.deleteById(id);
        } catch (Exception e) {
            throw new DataBaseUpdateException("Unable to delete Project with id='" + id + "'");
        }
    }

    @Override
    @Transactional
    public boolean existsById(Long id) {
        return projectRepository.existsById(id);
    }

    @Override
    @Transactional
    public ProjectEntity toEntity(ProjectDto dto) {
        if (dto == null) throw new InvalidDtoException("DTO is empty");
        if (dto.getName() == null || dto.getDescription() == null) throw new InvalidDtoException("Invalid DTO data");

        ProjectEntity result = null;
        if (dto.getId() == null) {
            result = new ProjectEntity();
            result.setId(0l);
            result.setCreated(new Date());
        } else {
            Optional<ProjectEntity> projectEntityOptional = projectRepository.findById(dto.getId());
            result = projectEntityOptional.orElseThrow(
                    () -> noSuchElementExceptionFactory.getNoSuchElementException(EntityType.PROJECT, "id", dto.getId()));
        }
        result.setName(dto.getName());
        result.setDescription(dto.getDescription());
        result.setStatus(EntityStatus.ACTIVE);
        result.setUpdated(new Date());

        return result;
    }

    @Override
    public ProjectDto toDto(ProjectEntity entity) {
        ProjectDto result = new ProjectDto();

        result.setId(entity.getId());
        result.setName(entity.getName());
        result.setDescription(entity.getDescription());
        return result;
    }

    @Override
    public List<ProjectDto> toDtoList(List<ProjectEntity> entityList) {
        List<ProjectDto> result =
                entityList.stream().map(projectEntity -> toDto(projectEntity)).collect(Collectors.toList());
        return result;
    }

}
