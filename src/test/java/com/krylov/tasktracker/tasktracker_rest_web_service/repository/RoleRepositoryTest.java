package com.krylov.tasktracker.tasktracker_rest_web_service.repository;

import com.krylov.tasktracker.tasktracker_rest_web_service.entity.RoleEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.enums.EntityStatus;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.enums.RoleType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void contextLoads() {
        assertThat(roleRepository).isNotNull();
    }

    @Test
    public void count() {
        Long actual = roleRepository.count();
        assertThat(actual).isEqualTo(3L);
    }

    @Test
    public void findRoleAdmin() {
        Optional<RoleEntity> optionalActual = roleRepository.findRoleAdmin();
        assertThat(optionalActual).isPresent();
        RoleEntity actual = optionalActual.get();
        assertThat(actual.getName()).isEqualTo(RoleType.ROLE_ADMIN);
        assertThat(actual.getStatus()).isEqualTo(EntityStatus.ACTIVE);
        assertThat(actual.getCreated()).isNotNull();
        assertThat(actual.getUpdated()).isNotNull();
    }

    @Test
    public void findRoleUser() {
        Optional<RoleEntity> optionalActual = roleRepository.findRoleUser();
        assertThat(optionalActual).isPresent();
        RoleEntity actual = optionalActual.get();
        assertThat(actual.getName()).isEqualTo(RoleType.ROLE_USER);
        assertThat(actual.getStatus()).isEqualTo(EntityStatus.ACTIVE);
        assertThat(actual.getCreated()).isNotNull();
        assertThat(actual.getUpdated()).isNotNull();
    }

    @Test
    public void findRoleManager() {
        Optional<RoleEntity> optionalActual = roleRepository.findRoleManager();
        assertThat(optionalActual).isPresent();
        RoleEntity actual = optionalActual.get();
        assertThat(actual.getName()).isEqualTo(RoleType.ROLE_MANAGER);
        assertThat(actual.getStatus()).isEqualTo(EntityStatus.ACTIVE);
        assertThat(actual.getCreated()).isNotNull();
        assertThat(actual.getUpdated()).isNotNull();
    }
}