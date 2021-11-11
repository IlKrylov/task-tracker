package com.krylov.tasktracker.tasktracker_rest_web_service.service;

import com.krylov.tasktracker.tasktracker_rest_web_service.dto.project.ProjectDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.ProjectEntity;

import java.util.List;
import java.util.Optional;

public interface ProjectService extends BaseEntityService<ProjectEntity, ProjectDto> {

    Optional<ProjectEntity> saveOrUpdate(ProjectDto dto);

    Optional<List<ProjectEntity>> findAllUserProjects(Long userId);

}
