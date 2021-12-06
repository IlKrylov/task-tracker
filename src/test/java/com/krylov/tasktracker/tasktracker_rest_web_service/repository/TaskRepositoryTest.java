package com.krylov.tasktracker.tasktracker_rest_web_service.repository;

import com.krylov.tasktracker.tasktracker_rest_web_service.entity.TaskEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.enums.EntityStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @BeforeAll
    static void setUp(@Autowired DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("test-data.sql"));
        } catch (Exception e) {
        }
    }

    @Test
    void contextLoads() {
        assertThat(taskRepository).isNotNull();
    }

    @Test
    void count() {
        Long actual = taskRepository.count();
        assertThat(actual).isEqualTo(21L);
    }

    @Test
    void findByName() {
        Optional<TaskEntity> optionalActual1 = taskRepository.findByName("Task1");
        assertThat(optionalActual1).isPresent();
        assertThat(optionalActual1.get().getId()).isEqualTo(1L);
        assertThat(optionalActual1.get().getName()).isEqualTo("Task1");
        assertThat(optionalActual1.get().getDescription()).isEqualTo("Task1 description");
        assertThat(optionalActual1.get().getStatus()).isEqualTo(EntityStatus.ACTIVE);

        Optional<TaskEntity> optionalActual10 = taskRepository.findByName("Task10");
        assertThat(optionalActual10).isPresent();

        Optional<TaskEntity> optionalActual20 = taskRepository.findByName("Task20");
        assertThat(optionalActual20).isPresent();

        Optional<TaskEntity> optionalActual30 = taskRepository.findByName("Task30");
        assertThat(optionalActual30).isEmpty();
    }

    @Test
    void existsById() {
        boolean actual1 = taskRepository.existsById(1l);
        assertThat(actual1).isEqualTo(true);

        boolean actual5 = taskRepository.existsById(5l);
        assertThat(actual5).isEqualTo(true);

        boolean actual10 = taskRepository.existsById(10l);
        assertThat(actual10).isEqualTo(true);

        boolean actual15 = taskRepository.existsById(15l);
        assertThat(actual15).isEqualTo(true);

        boolean actual20 = taskRepository.existsById(20l);
        assertThat(actual20).isEqualTo(true);

        boolean actual25 = taskRepository.existsById(25l);
        assertThat(actual25).isEqualTo(false);
    }

    @Test
    void findAllByProjectId() {
        //Project1
        List<TaskEntity> actual1 = taskRepository.findAllByProjectId(1l);
        assertThat(actual1.size()).isEqualTo(2);

        //Project2
        List<TaskEntity> actual2 = taskRepository.findAllByProjectId(2l);
        assertThat(actual2.size()).isEqualTo(4);

        //Project3
        List<TaskEntity> actual3 = taskRepository.findAllByProjectId(3l);
        assertThat(actual3.size()).isEqualTo(6);

        //Project4
        List<TaskEntity> actual4 = taskRepository.findAllByProjectId(4l);
        assertThat(actual4.size()).isEqualTo(4);

        //Project5
        List<TaskEntity> actual5 = taskRepository.findAllByProjectId(5l);
        assertThat(actual5.size()).isEqualTo(5);
    }

    @Test
    void findAllByUserId() {
        //User1
        List<TaskEntity> actual1 = taskRepository.findAllByUserId(1l);
        assertThat(actual1.size()).isEqualTo(6);

        //User2
        List<TaskEntity> actual2 = taskRepository.findAllByUserId(2l);
        assertThat(actual2.size()).isEqualTo(14);

        //User3
        List<TaskEntity> actual3 = taskRepository.findAllByUserId(3l);
        assertThat(actual3.size()).isEqualTo(1);

        //User4
        List<TaskEntity> actual4 = taskRepository.findAllByUserId(4l);
        assertThat(actual4.size()).isEqualTo(0);
    }

    @Test
    void findAllByProjectIdAndUserId() {
        //User1
        List<TaskEntity> actual1_1 = taskRepository.findAllByProjectIdAndUserId(1l, 1l);
        assertThat(actual1_1.size()).isEqualTo(1);
        List<TaskEntity> actual2_1 = taskRepository.findAllByProjectIdAndUserId(2l, 1l);
        assertThat(actual2_1.size()).isEqualTo(2);
        List<TaskEntity> actual3_1 = taskRepository.findAllByProjectIdAndUserId(3l, 1l);
        assertThat(actual3_1.size()).isEqualTo(3);
        List<TaskEntity> actual4_1 = taskRepository.findAllByProjectIdAndUserId(4l, 1l);
        assertThat(actual4_1.size()).isEqualTo(0);
        List<TaskEntity> actual5_1 = taskRepository.findAllByProjectIdAndUserId(5l, 1l);
        assertThat(actual5_1.size()).isEqualTo(0);

        //User2
        List<TaskEntity> actual1_2 = taskRepository.findAllByProjectIdAndUserId(1l, 2l);
        assertThat(actual1_2.size()).isEqualTo(0);
        List<TaskEntity> actual5_2 = taskRepository.findAllByProjectIdAndUserId(5l, 2l);
        assertThat(actual5_2.size()).isEqualTo(5);

        //User3
        List<TaskEntity> actual1_3 = taskRepository.findAllByProjectIdAndUserId(1l, 3l);
        assertThat(actual1_3.size()).isEqualTo(1);
        List<TaskEntity> actual2_3 = taskRepository.findAllByProjectIdAndUserId(2l, 3l);
        assertThat(actual2_3.size()).isEqualTo(0);

        //User4
        List<TaskEntity> actual1_4 = taskRepository.findAllByProjectIdAndUserId(1l, 4l);
        assertThat(actual1_4.size()).isEqualTo(0);
    }

    @Test
    void findByIdAndUserId() {
        //User1
        Optional<TaskEntity> optionalActual1_1 = taskRepository.findByIdAndUserId(1l, 1l);
        assertThat(optionalActual1_1).isPresent();
        Optional<TaskEntity> optionalActual10_1 = taskRepository.findByIdAndUserId(10l, 1l);
        assertThat(optionalActual10_1).isEmpty();
        Optional<TaskEntity> optionalActual20_1 = taskRepository.findByIdAndUserId(20l, 1l);
        assertThat(optionalActual20_1).isEmpty();
        Optional<TaskEntity> optionalActual30_1 = taskRepository.findByIdAndUserId(30l, 1l);
        assertThat(optionalActual30_1).isEmpty();

        //User2
        Optional<TaskEntity> optionalActual1_2 = taskRepository.findByIdAndUserId(1l, 2l);
        assertThat(optionalActual1_2).isEmpty();
        Optional<TaskEntity> optionalActual10_2 = taskRepository.findByIdAndUserId(10l, 2l);
        assertThat(optionalActual10_2).isPresent();
        Optional<TaskEntity> optionalActual20_2 = taskRepository.findByIdAndUserId(20l, 2l);
        assertThat(optionalActual20_2).isPresent();
        Optional<TaskEntity> optionalActual30_2 = taskRepository.findByIdAndUserId(30l, 2l);
        assertThat(optionalActual30_2).isEmpty();

        //User3
        Optional<TaskEntity> optionalActual1_3 = taskRepository.findByIdAndUserId(1l, 3l);
        assertThat(optionalActual1_3).isEmpty();
        Optional<TaskEntity> optionalActual10_3 = taskRepository.findByIdAndUserId(10l, 3l);
        assertThat(optionalActual10_3).isEmpty();
        Optional<TaskEntity> optionalActual20_3 = taskRepository.findByIdAndUserId(20l, 3l);
        assertThat(optionalActual20_3).isEmpty();
        Optional<TaskEntity> optionalActual30_3 = taskRepository.findByIdAndUserId(30l, 3l);
        assertThat(optionalActual30_3).isEmpty();

        //User4
        Optional<TaskEntity> optionalActual1_4 = taskRepository.findByIdAndUserId(1l, 4l);
        assertThat(optionalActual1_4).isEmpty();
        Optional<TaskEntity> optionalActual10_4 = taskRepository.findByIdAndUserId(10l, 4l);
        assertThat(optionalActual10_4).isEmpty();
        Optional<TaskEntity> optionalActual20_4 = taskRepository.findByIdAndUserId(20l, 4l);
        assertThat(optionalActual20_4).isEmpty();
        Optional<TaskEntity> optionalActual30_4 = taskRepository.findByIdAndUserId(30l, 4l);
        assertThat(optionalActual30_4).isEmpty();
    }
}