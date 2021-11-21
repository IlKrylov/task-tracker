package com.krylov.tasktracker.tasktracker_rest_web_service.service.impl;

import com.krylov.tasktracker.tasktracker_rest_web_service.dto.project.ProjectDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.ProjectEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.enums.EntityStatus;
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

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository,
                              UserRepository userRepository,
                              TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    @Transactional
    public Optional<ProjectEntity> saveOrUpdate(ProjectDto dto) {
        Optional<ProjectEntity> entityOptional = toEntity(dto);
        if (entityOptional.isEmpty()) return Optional.empty();

        ProjectEntity entity = projectRepository.save(entityOptional.get());
        return Optional.ofNullable(entity);
    }

    @Override
    @Transactional
    public List<ProjectEntity> getAll() {
        List<ProjectEntity> result = projectRepository.findAll();
        return result;
    }

    @Override
    @Transactional
    public Optional<ProjectEntity> findById(Long id) {
        Optional<ProjectEntity> result = projectRepository.findById(id);
        return result;
    }

    @Override
    public Optional<ProjectEntity> findByName(String name) {
        Optional<ProjectEntity> result = Optional.ofNullable(projectRepository.findByName(name));
        return result;
    }

    @Override
    @Transactional
    public Optional<List<ProjectEntity>> findAllUserProjects(Long userId) {
        if (!userRepository.existsById(userId)) return Optional.empty();
        List<ProjectEntity> result = projectRepository.findAllByUserId(userId);
        return Optional.ofNullable(result);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Optional<ProjectEntity> projectOptional = projectRepository.findById(id);
        if (projectOptional.isEmpty()) return;
        ProjectEntity project = projectOptional.get();

        project.getUsers().stream().forEach(userEntity -> userEntity.removeProject(project, new ArrayList<>()));
        project.getTasks().stream().forEach(taskEntity -> taskRepository.deleteById(taskEntity.getId()));

        projectRepository.deleteById(id);
    }

    @Override
    @Transactional
    public boolean existsById(Long id) {
        return projectRepository.existsById(id);
    }

    @Override
    @Transactional
    public Optional<ProjectEntity> toEntity(ProjectDto dto) {
        if (dto == null) return Optional.empty();
        Long id = dto.getId();

        ProjectEntity result = null;
        if (id == null) {
            result = new ProjectEntity();
            result.setId(0l);
            result.setCreated(new Date());
        } else {
            Optional<ProjectEntity> projectEntityOptional = projectRepository.findById(dto.getId());
            if (projectEntityOptional.isEmpty()) return Optional.empty();
            result = projectEntityOptional.get();
        }
        result.setName(dto.getName());
        result.setDescription(dto.getDescription());
        result.setStatus(EntityStatus.ACTIVE);
        result.setUpdated(new Date());

        return Optional.of(result);
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
