package com.krylov.tasktracker.tasktracker_rest_web_service.repository;

import com.krylov.tasktracker.tasktracker_rest_web_service.entity.UserEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    private static void setUp(@Autowired DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("test-data.sql"));
        } catch (Exception e) {
        }
    }

    @Test
    public void contextLoads() {
        assertThat(userRepository).isNotNull();
    }

    @Test
    public void count() {
        Long actual = userRepository.count();
        assertThat(actual).isEqualTo(4);
    }

    @Test
    public void findByUserName() {
        Optional<UserEntity> admin = userRepository.findByUserName("ADMIN");
        assertThat(admin).isPresent();

        Optional<UserEntity> user2 = userRepository.findByUserName("User2");
        assertThat(user2).isPresent();

        Optional<UserEntity> user3 = userRepository.findByUserName("User3");
        assertThat(user3).isPresent();

        Optional<UserEntity> user4 = userRepository.findByUserName("User4");
        assertThat(user4).isPresent();

        Optional<UserEntity> user100 = userRepository.findByUserName("User100");
        assertThat(user100).isEmpty();
    }

    @Test
    public void existsById() {
        assertThat(userRepository.existsById(1L)).isTrue();
        assertThat(userRepository.existsById(2L)).isTrue();
        assertThat(userRepository.existsById(3L)).isTrue();
        assertThat(userRepository.existsById(4L)).isTrue();
        assertThat(userRepository.existsById(100L)).isFalse();
    }

    @Test
    public void findAllByProjectId() {
        List<UserEntity> project1Users = userRepository.findAllByProjectId(1l);
        List<UserEntity> project2Users = userRepository.findAllByProjectId(2l);
        List<UserEntity> project3Users = userRepository.findAllByProjectId(3l);
        List<UserEntity> project4Users = userRepository.findAllByProjectId(4l);
        List<UserEntity> project5Users = userRepository.findAllByProjectId(5l);

        assertThat(project1Users.size()).isEqualTo(3);
        assertThat(project2Users.size()).isEqualTo(2);
        assertThat(project3Users.size()).isEqualTo(2);
        assertThat(project4Users.size()).isEqualTo(1);
        assertThat(project5Users.size()).isEqualTo(1);
    }

    @Test
    public void existsByUserName() {
        assertThat(userRepository.existsByUserName("ADMIN")).isTrue();
        assertThat(userRepository.existsByUserName("User2")).isTrue();
        assertThat(userRepository.existsByUserName("User3")).isTrue();
        assertThat(userRepository.existsByUserName("User4")).isTrue();
        assertThat(userRepository.existsByUserName("User100")).isFalse();
    }

    @Test
    public void existsByEmail() {
        assertThat(userRepository.existsByEmail("ADMIN")).isTrue();
        assertThat(userRepository.existsByEmail("user2@user.ru")).isTrue();
        assertThat(userRepository.existsByEmail("user3@user.ru")).isTrue();
        assertThat(userRepository.existsByEmail("user4@user.ru")).isTrue();
        assertThat(userRepository.existsByEmail("user100@user.ru")).isFalse();
    }
}