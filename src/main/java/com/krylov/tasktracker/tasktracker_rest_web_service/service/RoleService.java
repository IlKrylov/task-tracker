package com.krylov.tasktracker.tasktracker_rest_web_service.service;

import com.krylov.tasktracker.tasktracker_rest_web_service.dto.role.RoleDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.RoleEntity;

import java.util.List;
import java.util.Optional;

public interface RoleService extends BaseEntityService<RoleEntity, RoleDto> {

    List<RoleEntity> findAllUserRoles(Long userId);

    RoleEntity findRoleUser();

    RoleEntity findRoleAdmin();

    RoleEntity findRoleManager();

}
