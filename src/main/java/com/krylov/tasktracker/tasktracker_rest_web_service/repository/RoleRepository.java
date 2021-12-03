package com.krylov.tasktracker.tasktracker_rest_web_service.repository;

import com.krylov.tasktracker.tasktracker_rest_web_service.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByName(String name);

    boolean existsById(Long id);

    @Query("SELECT r FROM RoleEntity r WHERE r.name = 'ROLE_ADMIN'")
    Optional<RoleEntity> findRoleAdmin();

    @Query("SELECT r FROM RoleEntity r WHERE r.name = 'ROLE_USER'")
    Optional<RoleEntity> findRoleUser();

    @Query("SELECT r FROM RoleEntity r WHERE r.name = 'ROLE_MANAGER'")
    Optional<RoleEntity> findRoleManager();
}
