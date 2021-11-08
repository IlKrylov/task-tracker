package com.krylov.tasktracker.tasktracker_rest_web_service.repository;

import com.krylov.tasktracker.tasktracker_rest_web_service.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    ProjectEntity findByName(String name);

    boolean existsById (Long id);

    @Query("select p from ProjectEntity p join p.users u where u.id = :userId")
    List<ProjectEntity> findAllByUserId(@Param("userId") Long userId);
}
