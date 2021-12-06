package com.krylov.tasktracker.tasktracker_rest_web_service.repository;

import com.krylov.tasktracker.tasktracker_rest_web_service.entity.ProjectEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.enums.EntityStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.jdbc.Sql;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @BeforeAll
    static void setUp(@Autowired DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("test-data.sql"));
        } catch (Exception e){
        }
    }

    @Test
    void contextLoads() {
        assertThat(projectRepository).isNotNull();
    }

    @Test
    void count() {
        Long actual = projectRepository.count();
        assertThat(actual).isEqualTo(5L);
    }

    @Test
    void findByName() {
        Optional<ProjectEntity> optionalActual1 = projectRepository.findByName("Project1");
        assertThat(optionalActual1).isPresent();
        assertThat(optionalActual1.get().getId()).isEqualTo(1L);
        assertThat(optionalActual1.get().getName()).isEqualTo("Project1");
        assertThat(optionalActual1.get().getDescription()).isEqualTo("TestProject1");
        assertThat(optionalActual1.get().getStatus()).isEqualTo(EntityStatus.ACTIVE);

        Optional<ProjectEntity> optionalActual2 = projectRepository.findByName("Project2");
        assertThat(optionalActual2).isPresent();

        Optional<ProjectEntity> optionalActual3 = projectRepository.findByName("Project3");
        assertThat(optionalActual3).isPresent();

        Optional<ProjectEntity> optionalActual4 = projectRepository.findByName("Project4");
        assertThat(optionalActual4).isPresent();

        Optional<ProjectEntity> optionalActual5 = projectRepository.findByName("Project5");
        assertThat(optionalActual5).isPresent();

        Optional<ProjectEntity> optionalActual100 = projectRepository.findByName("Project100");
        assertThat(optionalActual100).isEmpty();
    }

    @Test
    void existsById() {
        boolean actual1 = projectRepository.existsById(1l);
        assertThat(actual1).isEqualTo(true);

        boolean actual2 = projectRepository.existsById(2l);
        assertThat(actual2).isEqualTo(true);

        boolean actual3 = projectRepository.existsById(3l);
        assertThat(actual3).isEqualTo(true);

        boolean actual4 = projectRepository.existsById(4l);
        assertThat(actual4).isEqualTo(true);

        boolean actual5 = projectRepository.existsById(5l);
        assertThat(actual5).isEqualTo(true);

        boolean actual6 = projectRepository.existsById(6l);
        assertThat(actual6).isEqualTo(false);
    }

    @Test
    void findAllByUserId() {
        List<ProjectEntity> actualProjects1 = projectRepository.findAllByUserId(1l);
        assertThat(actualProjects1.size()).isEqualTo(3);

        List<ProjectEntity> actualProjects2 = projectRepository.findAllByUserId(2l);
        assertThat(actualProjects2.size()).isEqualTo(5);

        List<ProjectEntity> actualProjects3 = projectRepository.findAllByUserId(3l);
        assertThat(actualProjects3.size()).isEqualTo(1);

        List<ProjectEntity> actualProjects4 = projectRepository.findAllByUserId(4l);
        assertThat(actualProjects4.size()).isEqualTo(0);
    }
}