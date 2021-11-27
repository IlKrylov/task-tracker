package com.krylov.tasktracker.tasktracker_rest_web_service.service;

import com.krylov.tasktracker.tasktracker_rest_web_service.dto.project.ProjectDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.ProjectEntity;

import java.util.List;

public interface ProjectService extends BaseEntityService<ProjectEntity, ProjectDto> {

    ProjectEntity saveOrUpdate(ProjectDto dto);

    List<ProjectEntity> findAllUserProjects(Long userId);

}
