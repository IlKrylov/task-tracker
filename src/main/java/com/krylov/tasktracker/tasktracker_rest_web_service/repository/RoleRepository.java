package com.krylov.tasktracker.tasktracker_rest_web_service.repository;

import com.krylov.tasktracker.tasktracker_rest_web_service.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByName(String name);

    boolean existsById(Long id);

    @Query(nativeQuery = true, value = "SELECT * FROM roles s WHERE s.name = 'ROLE_ADMIN' LIMIT 1")
    Optional<RoleEntity> findRoleAdmin();

    @Query(nativeQuery = true, value = "SELECT * FROM roles s WHERE s.name = 'ROLE_USER' LIMIT 1")
    Optional<RoleEntity> findRoleUser();

    @Query(nativeQuery = true, value = "SELECT * FROM roles s WHERE s.name = 'ROLE_MANAGER' LIMIT 1")
    Optional<RoleEntity> findRoleManager();
}
