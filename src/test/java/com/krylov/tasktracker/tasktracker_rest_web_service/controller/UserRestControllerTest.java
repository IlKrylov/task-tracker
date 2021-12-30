package com.krylov.tasktracker.tasktracker_rest_web_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.jwt.JwtDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.project.ProjectDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.task.TaskDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.task.TaskStatusChangeRequestDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.UserAuthenticationRequestDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.enums.EntityStatus;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRestController userRestController;

    @Value("${root.admin.password}")
    private String adminPassword;

    private static String jwtTokenAdmin;

    @BeforeAll
    private static void setUp(@Autowired DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("test-data.sql"));
        } catch (Exception e) {
        }
    }

    @BeforeEach
    private void setUp() {
        if (jwtTokenAdmin == null) {
            String url = "http://localhost:" + port + "/api/v1/authentication";
            UserAuthenticationRequestDto authenticationRequestDto = new UserAuthenticationRequestDto("ADMIN", adminPassword);
            HttpEntity<UserAuthenticationRequestDto> request = new HttpEntity<>(authenticationRequestDto, new HttpHeaders());
            ResponseEntity<JwtDto> response = restTemplate.postForEntity(url, request, JwtDto.class);
            jwtTokenAdmin = "Bearer " + response.getBody().getToken();
        }
    }

    @Test
    public void contextLoads() {
        assertThat(userRestController).isNotNull();
    }

    @Test
    public void securityWorks(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "invalidToken");
        HttpEntity request = new HttpEntity(httpHeaders);

        ResponseEntity<String> responseGetProjects = restTemplate.exchange(
                "http://localhost:" + port + "/api/v1/user/projects", HttpMethod.GET, request,String.class);
        assertThat(responseGetProjects.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        ResponseEntity<String> responseGetTasks = restTemplate.exchange(
                "http://localhost:" + port + "/api/v1/user/tasks", HttpMethod.GET, request,String.class);
        assertThat(responseGetTasks.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        ResponseEntity<String> responseGetTasksOnProject = restTemplate.exchange(
                "http://localhost:" + port + "/api/v1/user/projects/1/tasks", HttpMethod.GET, request,String.class);
        assertThat(responseGetTasksOnProject.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        ResponseEntity<String> responseUpdateTasks = restTemplate.exchange(
                "http://localhost:" + port + "/api/v1/user/tasks", HttpMethod.POST, request,String.class);
        assertThat(responseUpdateTasks.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void getAdminProjects() {
        String url = "http://localhost:" + port + "/api/v1/user/projects";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", jwtTokenAdmin);
        HttpEntity request = new HttpEntity(httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<ProjectDto> projectDtoList = null;
        try {
            projectDtoList = objectMapper.readValue(response.getBody(), List.class);
        } catch (Exception e) {
        }
        assertThat(projectDtoList).isNotNull();
        assertThat(projectDtoList.size()).isEqualTo(3);
    }

    @Test
    public void getAdminTasks() {
        String url = "http://localhost:" + port + "/api/v1/user/tasks";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", jwtTokenAdmin);
        HttpEntity request = new HttpEntity(httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskDto> taskDtoList = null;
        try {
            taskDtoList = objectMapper.readValue(response.getBody(), List.class);
        } catch (Exception e) {
        }
        assertThat(taskDtoList).isNotNull();
        assertThat(taskDtoList.size()).isEqualTo(6);
    }

    @Test
    public void getAllAdminTasksOnAvailableProject() {
        Long projectId = 3l;
        String url = "http://localhost:" + port + "/api/v1/user/projects/" + projectId + "/tasks";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", jwtTokenAdmin);
        HttpEntity request = new HttpEntity(httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskDto> taskDtoList = null;
        try {
            taskDtoList = objectMapper.readValue(response.getBody(), List.class);
        } catch (Exception e) {
        }
        assertThat(taskDtoList).isNotNull();
        assertThat(taskDtoList.size()).isEqualTo(3);
    }

    @Test
    public void getAllAdminTasksOnMissedProject() {
        Long projectId = 1000l;
        String url = "http://localhost:" + port + "/api/v1/user/projects/" + projectId + "/tasks";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", jwtTokenAdmin);
        HttpEntity request = new HttpEntity(httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void updateAssignedTask() {
        Long taskId = 1l;
        TaskStatusChangeRequestDto requestDto = new TaskStatusChangeRequestDto(taskId, EntityStatus.NOT_ACTIVE);
        String url = "http://localhost:" + port + "/api/v1/user/tasks";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", jwtTokenAdmin);
        HttpEntity<TaskStatusChangeRequestDto> request = new HttpEntity<>(requestDto, httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        TaskDto taskDto = null;
        try {
            taskDto = objectMapper.readValue(response.getBody(), TaskDto.class);
        } catch (Exception e) {
        }
        assertThat(taskDto).isNotNull();
        assertThat(taskDto.getEntityStatus()).isEqualTo(EntityStatus.NOT_ACTIVE);
    }

    @Test
    public void updateUnassignedTask() {
        Long taskId = 100l;
        TaskStatusChangeRequestDto requestDto = new TaskStatusChangeRequestDto(taskId, EntityStatus.NOT_ACTIVE);
        String url = "http://localhost:" + port + "/api/v1/user/tasks";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", jwtTokenAdmin);
        HttpEntity<TaskStatusChangeRequestDto> request = new HttpEntity<>(requestDto, httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}