package com.krylov.tasktracker.tasktracker_rest_web_service.service;

import com.krylov.tasktracker.tasktracker_rest_web_service.dto.RoleDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.RoleEntity;

import java.util.List;
import java.util.Optional;

public interface RoleService extends BaseEntityService<RoleEntity, RoleDto> {

    Optional<List<RoleEntity>> findAllUserRoles(Long userId);

    Optional<RoleEntity> findRoleUser();

    Optional<RoleEntity> findRoleAdmin();

    Optional<RoleEntity> findRoleManager();

}
