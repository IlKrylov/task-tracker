package com.krylov.tasktracker.tasktracker_rest_web_service.repository;

import com.krylov.tasktracker.tasktracker_rest_web_service.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUserName(String userName);

    boolean existsById(Long id);

    @Query("select u from UserEntity u join u.projects p where p.id = :projectId")
    List<UserEntity> findAllByProjectId(@Param("projectId") Long projectId);

    boolean existsByUserName(String userName);

    boolean existsByEmail(String email);

}
