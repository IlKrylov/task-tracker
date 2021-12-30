package com.krylov.tasktracker.tasktracker_rest_web_service.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.jwt.JwtDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.project.ProjectDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.role.RoleDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.task.TaskDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.UserAuthenticationRequestDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.UserInfoDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.UserLinksUpdateRequestDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.enums.ChangeType;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.enums.EntityStatus;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AdminRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AdminRestController adminRestController;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtTokenAdmin;

    @BeforeEach
    private void setUp(@Autowired DataSource dataSource, @Value("${root.admin.password}") String adminPassword) {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("test-data.sql"));
        } catch (Exception e) {
        }
        if (jwtTokenAdmin == null) {
            String url = "http://localhost:" + port + "/api/v1/authentication";
            UserAuthenticationRequestDto requestDto = new UserAuthenticationRequestDto("ADMIN", adminPassword);
            HttpEntity<UserAuthenticationRequestDto> request = new HttpEntity<>(requestDto, new HttpHeaders());
            ResponseEntity<JwtDto> response = restTemplate.postForEntity(url, request, JwtDto.class);
            jwtTokenAdmin = "Bearer " + response.getBody().getToken();
        }
    }

    @Test
    public void contextLoads() {
        assertThat(adminRestController).isNotNull();
    }

    @Test
    public void securityWorks() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "invalidToken");
        HttpEntity request = new HttpEntity(httpHeaders);

        ResponseEntity<String> responseRoles = restTemplate.exchange(
                "http://localhost:" + port + "/api/v1/admin/roles", HttpMethod.GET, request, String.class);
        assertThat(responseRoles.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        ResponseEntity<String> responseUsers = restTemplate.exchange(
                "http://localhost:" + port + "/api/v1/admins/users", HttpMethod.GET, request, String.class);
        assertThat(responseUsers.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        ResponseEntity<String> responseProjects = restTemplate.exchange(
                "http://localhost:" + port + "/api/v1/admin/projects", HttpMethod.GET, request, String.class);
        assertThat(responseProjects.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        ResponseEntity<String> responseTasks = restTemplate.exchange(
                "http://localhost:" + port + "/api/v1/admin/tasks", HttpMethod.GET, request, String.class);
        assertThat(responseTasks.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void getAllRoles() {
        ResponseEntity<String> responseEntity =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/roles", HttpMethod.GET);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<RoleDto> dtoList =
                (List<RoleDto>) getObjectFromString(responseEntity.getBody(), new TypeReference<List<RoleDto>>() {
                });
        assertThat(dtoList).isNotNull();
        assertThat(dtoList.size()).isEqualTo(3);
    }

    @Test
    public void getAllRolesOnUserById() {
        ResponseEntity<String> responseEntityAdmin =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/users/" + 1l + "/roles", HttpMethod.GET);
        assertThat(responseEntityAdmin.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<RoleDto> dtoListAdmin =
                (List<RoleDto>) getObjectFromString(responseEntityAdmin.getBody(), new TypeReference<List<RoleDto>>() {
                });
        assertThat(dtoListAdmin).isNotNull();
        assertThat(dtoListAdmin.size()).isEqualTo(2);

        ResponseEntity<String> responseEntityUser =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/users/" + 2l + "/roles", HttpMethod.GET);
        assertThat(responseEntityUser.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<RoleDto> dtoListUser =
                (List<RoleDto>) getObjectFromString(responseEntityUser.getBody(), new TypeReference<List<RoleDto>>() {
                });
        assertThat(dtoListUser).isNotNull();
        assertThat(dtoListUser.size()).isEqualTo(1);
    }

    @Test
    public void getAllUsers() {
        ResponseEntity<String> responseEntity =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/users/", HttpMethod.GET);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<UserInfoDto> dtoList =
                (List<UserInfoDto>) getObjectFromString(responseEntity.getBody(), new TypeReference<List<UserInfoDto>>() {
                });
        assertThat(dtoList).isNotNull();
        assertThat(dtoList.size()).isEqualTo(4);
    }

    @Test
    public void getUserById() {
        ResponseEntity<String> responseEntity2 =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/users/2", HttpMethod.GET);
        assertThat(responseEntity2.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserInfoDto dto2 =
                (UserInfoDto) getObjectFromString(responseEntity2.getBody(), new TypeReference<UserInfoDto>() {
                });
        assertThat(dto2).isNotNull();
        assertThat(dto2.getUserName()).isEqualTo("User2");

        ResponseEntity<String> responseEntity100 =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/users/100", HttpMethod.GET);
        assertThat(responseEntity100.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getAllUsersOnProjectById() {
        ResponseEntity<String> responseEntity1 =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/projects/1/users", HttpMethod.GET);
        assertThat(responseEntity1.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<UserInfoDto> dto1 =
                (List<UserInfoDto>) getObjectFromString(responseEntity1.getBody(), new TypeReference<List<UserInfoDto>>() {
                });
        assertThat(dto1).isNotNull();
        assertThat(dto1.size()).isEqualTo(3);

        ResponseEntity<String> responseEntity100 =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/projects/100/users", HttpMethod.GET);
        assertThat(responseEntity100.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void updateUserLinks() {
        //StateBefore
        ResponseEntity<String> responseEntityUser3RolesBefore =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/users/3/roles", HttpMethod.GET);
        assertThat(responseEntityUser3RolesBefore.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<RoleDto> user3RolesBefore =
                (List<RoleDto>) getObjectFromString(responseEntityUser3RolesBefore.getBody(), new TypeReference<List<RoleDto>>() {
                });
        assertThat(user3RolesBefore).isNotNull();
        assertThat(user3RolesBefore.size()).isEqualTo(1);

        ResponseEntity<String> responseEntityUser3ProjectsBefore =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/users/3/projects", HttpMethod.GET);
        assertThat(responseEntityUser3ProjectsBefore.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<ProjectDto> user3ProjectsBefore =
                (List<ProjectDto>) getObjectFromString(responseEntityUser3ProjectsBefore.getBody(), new TypeReference<List<ProjectDto>>() {
                });
        assertThat(user3ProjectsBefore).isNotNull();
        assertThat(user3ProjectsBefore.size()).isEqualTo(1);

        ResponseEntity<String> responseEntityUser3TasksBefore =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/users/3/tasks", HttpMethod.GET);
        assertThat(responseEntityUser3TasksBefore.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskDto> user3TaskBefore =
                (List<TaskDto>) getObjectFromString(responseEntityUser3TasksBefore.getBody(), new TypeReference<List<TaskDto>>() {
                });
        assertThat(user3TaskBefore).isNotNull();
        assertThat(user3TaskBefore.size()).isEqualTo(1);

        ResponseEntity<String> responseEntityUser1TasksBefore =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/users/1/tasks", HttpMethod.GET);
        assertThat(responseEntityUser1TasksBefore.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskDto> user1TaskBefore =
                (List<TaskDto>) getObjectFromString(responseEntityUser1TasksBefore.getBody(), new TypeReference<List<TaskDto>>() {
                });
        assertThat(user1TaskBefore).isNotNull();
        assertThat(user1TaskBefore.size()).isEqualTo(6);

        // UpdateLinks
        UserLinksUpdateRequestDto requestDto = new UserLinksUpdateRequestDto();
        requestDto.setId(3l);

        Map<Long, ChangeType> roleChanges = new HashMap<>();
        roleChanges.put(1l, ChangeType.ADD);
        roleChanges.put(2l, ChangeType.ADD);
        roleChanges.put(3l, ChangeType.ADD);
        roleChanges.put(4l, ChangeType.ADD); //invalid
        roleChanges.put(5l, ChangeType.REMOVE); //invalid

        Map<Long, ChangeType> projectChanges = new HashMap<>();
        projectChanges.put(1l, ChangeType.REMOVE);
        projectChanges.put(2l, ChangeType.ADD);
        projectChanges.put(3l, ChangeType.ADD);
        projectChanges.put(4l, ChangeType.ADD);
        projectChanges.put(5l, ChangeType.ADD);
        projectChanges.put(100l, ChangeType.ADD); //invalid
        projectChanges.put(101l, ChangeType.REMOVE); //invalid

        Map<Long, ChangeType> taskChanges = new HashMap<>();
        taskChanges.put(21l, ChangeType.REMOVE);
        taskChanges.put(1l, ChangeType.ADD); //can't be added
        taskChanges.put(2l, ChangeType.ADD);
        taskChanges.put(3l, ChangeType.ADD);
        taskChanges.put(4l, ChangeType.ADD);
        taskChanges.put(5l, ChangeType.ADD);
        taskChanges.put(6l, ChangeType.ADD);
        taskChanges.put(100l, ChangeType.ADD); //invalid
        taskChanges.put(101l, ChangeType.REMOVE);//invalid

        requestDto.setRoleChanges(roleChanges);
        requestDto.setProjectChanges(projectChanges);
        requestDto.setTaskChanges(taskChanges);

        String url = "http://localhost:" + port + "/api/v1/admin/users";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", jwtTokenAdmin);
        HttpEntity<UserLinksUpdateRequestDto> request = new HttpEntity<>(requestDto, httpHeaders);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        //State after update links
        ResponseEntity<String> responseEntityUser3RolesAfter =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/users/3/roles", HttpMethod.GET);
        assertThat(responseEntityUser3RolesAfter.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<RoleDto> user3RolesAfter =
                (List<RoleDto>) getObjectFromString(responseEntityUser3RolesAfter.getBody(), new TypeReference<List<RoleDto>>() {
                });
        assertThat(user3RolesAfter).isNotNull();
        //2 valid roles added (1 existed before), 2 invalid ignored
        assertThat(user3RolesAfter.size()).isEqualTo(3);

        ResponseEntity<String> responseEntityUser3ProjectsAfter =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/users/3/projects", HttpMethod.GET);
        assertThat(responseEntityUser3ProjectsAfter.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<ProjectDto> user3ProjectsAfter =
                (List<ProjectDto>) getObjectFromString(responseEntityUser3ProjectsAfter.getBody(), new TypeReference<List<ProjectDto>>() {
                });
        assertThat(user3ProjectsAfter).isNotNull();
        //1 project deleted and 4 added, 2 invalid ignored
        assertThat(user3ProjectsAfter.size()).isEqualTo(4);

        ResponseEntity<String> responseEntityUser3TasksAfter =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/users/3/tasks", HttpMethod.GET);
        assertThat(responseEntityUser3TasksAfter.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskDto> user3TasksAfter =
                (List<TaskDto>) getObjectFromString(responseEntityUser3TasksAfter.getBody(), new TypeReference<List<TaskDto>>() {
                });
        assertThat(user3TasksAfter).isNotNull();
        //1 removed, 5/6 added, 1 can't be added as project with id = 1 is not assigned, 2 invalid ignored
        assertThat(user3TasksAfter.size()).isEqualTo(5);

        ResponseEntity<String> responseEntityUser1TasksAfter =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/users/1/tasks", HttpMethod.GET);
        assertThat(responseEntityUser1TasksAfter.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskDto> user1TasksAfter =
                (List<TaskDto>) getObjectFromString(responseEntityUser1TasksAfter.getBody(), new TypeReference<List<TaskDto>>() {
                });
        assertThat(user1TasksAfter).isNotNull();
        //5 tasks were moved from user1 to user3, task1 can't be moved to user3 and still assigned to user1
        assertThat(user1TasksAfter.size()).isEqualTo(1);
    }

    @Test
    public void deleteUserById() {
        //State before delete
        ResponseEntity<String> responseEntityGetUser2Before =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/users/2", HttpMethod.GET);
        assertThat(responseEntityGetUser2Before.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<String> responseEntityGetUser2ProjectsBefore =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/users/2/projects", HttpMethod.GET);
        assertThat(responseEntityGetUser2ProjectsBefore.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<ProjectDto> user2ProjectsBefore =
                (List<ProjectDto>) getObjectFromString(responseEntityGetUser2ProjectsBefore.getBody(), new TypeReference<List<ProjectDto>>() {
                });
        assertThat(user2ProjectsBefore).isNotNull();
        assertThat(user2ProjectsBefore.size()).isEqualTo(5);

        ResponseEntity<String> responseEntityGetUser2TasksBefore =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/users/2/tasks", HttpMethod.GET);
        assertThat(responseEntityGetUser2TasksBefore.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskDto> user2TasksBefore =
                (List<TaskDto>) getObjectFromString(responseEntityGetUser2TasksBefore.getBody(), new TypeReference<List<TaskDto>>() {
                });
        assertThat(user2TasksBefore).isNotNull();
        assertThat(user2TasksBefore.size()).isEqualTo(14);

        //Delete user with id = 2
        ResponseEntity<String> responseEntityDeleteUser2 =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/users/2", HttpMethod.DELETE);
        assertThat(responseEntityDeleteUser2.getStatusCode()).isEqualTo(HttpStatus.OK);

        //State after delete
        ResponseEntity<String> responseEntityGetUser2After =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/users/2", HttpMethod.GET);
        assertThat(responseEntityGetUser2After.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        //Check that user's projects were not deleted
        for (ProjectDto projectDto : user2ProjectsBefore) {
            ResponseEntity<String> responseEntityProject =
                    getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/projects/" + projectDto.getId(), HttpMethod.GET);
            assertThat(responseEntityProject.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        //Check that user's tasks were not deleted and have empty userId field
        for (TaskDto taskDto : user2TasksBefore) {
            ResponseEntity<String> responseEntityTask =
                    getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/tasks/" + taskDto.getId(), HttpMethod.GET);
            assertThat(responseEntityTask.getStatusCode()).isEqualTo(HttpStatus.OK);
            TaskDto currentDto =
                    (TaskDto) getObjectFromString(responseEntityTask.getBody(), new TypeReference<TaskDto>() {
                    });
            assertThat(currentDto).isNotNull();
            assertThat(currentDto.getUserId()).isNull();
        }
    }

    @Test
    public void getAllProjects() {
        ResponseEntity<String> responseEntity =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/projects/", HttpMethod.GET);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<ProjectDto> dtoList =
                (List<ProjectDto>) getObjectFromString(responseEntity.getBody(), new TypeReference<List<ProjectDto>>() {
                });
        assertThat(dtoList).isNotNull();
        assertThat(dtoList.size()).isEqualTo(5);
    }

    @Test
    public void getProjectById() {
        ResponseEntity<String> responseEntity2 =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/projects/2", HttpMethod.GET);
        assertThat(responseEntity2.getStatusCode()).isEqualTo(HttpStatus.OK);
        ProjectDto dto2 =
                (ProjectDto) getObjectFromString(responseEntity2.getBody(), new TypeReference<ProjectDto>() {
                });
        assertThat(dto2).isNotNull();
        assertThat(dto2.getName()).isEqualTo("Project2");

        ResponseEntity<String> responseEntity100 =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/projects/100", HttpMethod.GET);
        assertThat(responseEntity100.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getAllProjectsOnUserById() {
        ResponseEntity<String> responseEntity1 =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/users/1/projects", HttpMethod.GET);
        assertThat(responseEntity1.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<ProjectDto> dto1 =
                (List<ProjectDto>) getObjectFromString(responseEntity1.getBody(), new TypeReference<List<ProjectDto>>() {
                });
        assertThat(dto1).isNotNull();
        assertThat(dto1.size()).isEqualTo(3);

        ResponseEntity<String> responseEntity100 =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/users/100/projects", HttpMethod.GET);
        assertThat(responseEntity100.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }

    @Test
    public void createOrUpdateProject() {
        //State before update
        ResponseEntity<String> responseEntityGetAllProjectsBefore =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/projects/", HttpMethod.GET);
        assertThat(responseEntityGetAllProjectsBefore.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<ProjectDto> projectsBefore =
                (List<ProjectDto>) getObjectFromString(responseEntityGetAllProjectsBefore.getBody(), new TypeReference<List<ProjectDto>>() {
                });
        assertThat(projectsBefore).isNotNull();
        assertThat(projectsBefore.size()).isEqualTo(5);
        ProjectDto project1Before = projectsBefore.stream().filter(projectDto -> projectDto.getId() == 1l).findFirst().get();
        assertThat(project1Before.getName()).isEqualTo("Project1");

        //Update
        //Modify existing project
        project1Before.setName("Project1_modified");
        String url = "http://localhost:" + port + "/api/v1/admin/projects";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", jwtTokenAdmin);
        HttpEntity<ProjectDto> requestModify = new HttpEntity<>(project1Before, httpHeaders);
        ResponseEntity<String> responseModify = restTemplate.postForEntity(url, requestModify, String.class);
        assertThat(responseModify.getStatusCode()).isEqualTo(HttpStatus.OK);
        //Create new project
        ProjectDto newProject = new ProjectDto();
        newProject.setName("NewProject");
        newProject.setDescription("NewProject");
        HttpEntity<ProjectDto> requestCreate = new HttpEntity<>(newProject, httpHeaders);
        ResponseEntity<String> responseCreate = restTemplate.postForEntity(url, requestCreate, String.class);
        assertThat(responseCreate.getStatusCode()).isEqualTo(HttpStatus.OK);

        //State after update
        ResponseEntity<String> responseEntityGetAllProjectsAfter =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/projects/", HttpMethod.GET);
        assertThat(responseEntityGetAllProjectsAfter.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<ProjectDto> projectsAfter =
                (List<ProjectDto>) getObjectFromString(responseEntityGetAllProjectsAfter.getBody(), new TypeReference<List<ProjectDto>>() {
                });
        assertThat(projectsAfter).isNotNull();
        assertThat(projectsAfter.size()).isEqualTo(6);
        ProjectDto project1After = projectsAfter.stream().filter(projectDto -> projectDto.getId() == 1l).findFirst().get();
        assertThat(project1After.getName()).isEqualTo("Project1_modified");
    }

    @Test
    public void deleteProjectById() {

        //State before delete
        ResponseEntity<String> responseEntityGetProject1Before =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/projects/1", HttpMethod.GET);
        assertThat(responseEntityGetProject1Before.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<String> responseEntityGetTasksOnProject1 =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/projects/1/tasks", HttpMethod.GET);
        List<TaskDto> project1Tasks =
                (List<TaskDto>) getObjectFromString(responseEntityGetTasksOnProject1.getBody(), new TypeReference<List<TaskDto>>() {
                });
        assertThat(project1Tasks).isNotNull();
        assertThat(project1Tasks.size()).isEqualTo(2);

        ResponseEntity<String> responseEntityGetUsersOnProject1 =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/projects/1/users", HttpMethod.GET);
        List<UserInfoDto> project1Users =
                (List<UserInfoDto>) getObjectFromString(responseEntityGetUsersOnProject1.getBody(), new TypeReference<List<UserInfoDto>>() {
                });
        assertThat(project1Users).isNotNull();
        assertThat(project1Users.size()).isEqualTo(3);

        ResponseEntity<String> responseEntityGetUser1Projects =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/users/1/projects", HttpMethod.GET);
        List<ProjectDto> user1ProjectsBefore =
                (List<ProjectDto>) getObjectFromString(responseEntityGetUser1Projects.getBody(), new TypeReference<List<ProjectDto>>() {
                });
        assertThat(user1ProjectsBefore).isNotNull();
        assertThat(user1ProjectsBefore.size()).isEqualTo(3);

        //Delete project with id = 1
        ResponseEntity<String> responseEntityDelete1 =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/projects/1", HttpMethod.DELETE);
        assertThat(responseEntityDelete1.getStatusCode()).isEqualTo(HttpStatus.OK);

        //State after delete
        ResponseEntity<String> responseEntityGet1After =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/projects/1", HttpMethod.GET);
        assertThat(responseEntityGet1After.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        //Check that project's tasks were deleted
        for (TaskDto project1Task : project1Tasks) {
            String url = "http://localhost:" + port + "/api/v1/admin/tasks/" + project1Task.getId();
            ResponseEntity<String> responseEntityGetTaskAfter =
                    getResponseEntityWithValidAdminToken(url, HttpMethod.GET);
            assertThat(responseEntityGetTaskAfter.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        //Check that project's users were not deleted
        for (UserInfoDto project1User : project1Users) {
            String url = "http://localhost:" + port + "/api/v1/admin/users/" + project1User.getId();
            ResponseEntity<String> responseEntityGetUserAfter =
                    getResponseEntityWithValidAdminToken(url, HttpMethod.GET);
            assertThat(responseEntityGetUserAfter.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        //Check that user with id= 1 has one project less
        ResponseEntity<String> responseUser1Projects =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/users/1/projects", HttpMethod.GET);
        List<ProjectDto> user1ProjectsAfter =
                (List<ProjectDto>) getObjectFromString(responseUser1Projects.getBody(), new TypeReference<List<ProjectDto>>() {
                });
        assertThat(user1ProjectsAfter).isNotNull();
        assertThat(user1ProjectsAfter.size()).isEqualTo(2);
    }

    @Test
    public void getAllTasks() {
        ResponseEntity<String> responseEntity =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/tasks/", HttpMethod.GET);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskDto> dtoList =
                (List<TaskDto>) getObjectFromString(responseEntity.getBody(), new TypeReference<List<TaskDto>>() {
                });
        assertThat(dtoList).isNotNull();
        assertThat(dtoList.size()).isEqualTo(21);
    }

    @Test
    public void getTaskById() {
        ResponseEntity<String> responseEntity2 =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/tasks/2", HttpMethod.GET);
        assertThat(responseEntity2.getStatusCode()).isEqualTo(HttpStatus.OK);
        TaskDto dto2 =
                (TaskDto) getObjectFromString(responseEntity2.getBody(), new TypeReference<TaskDto>() {
                });
        assertThat(dto2).isNotNull();
        assertThat(dto2.getName()).isEqualTo("Task2");

        ResponseEntity<String> responseEntity100 =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/tasks/100", HttpMethod.GET);
        assertThat(responseEntity100.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }

    @Test
    public void getAllTasksOnProjectById() {
        ResponseEntity<String> responseEntity1 =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/projects/1/tasks", HttpMethod.GET);
        assertThat(responseEntity1.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskDto> dto1 =
                (List<TaskDto>) getObjectFromString(responseEntity1.getBody(), new TypeReference<List<TaskDto>>() {
                });
        assertThat(dto1).isNotNull();
        assertThat(dto1.size()).isEqualTo(2);

        ResponseEntity<String> responseEntity100 =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/projects/100/tasks", HttpMethod.GET);
        assertThat(responseEntity100.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getAllTasksOnUserById() {
        ResponseEntity<String> responseEntity1 =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/users/1/tasks", HttpMethod.GET);
        assertThat(responseEntity1.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskDto> dto1 =
                (List<TaskDto>) getObjectFromString(responseEntity1.getBody(), new TypeReference<List<TaskDto>>() {
                });
        assertThat(dto1).isNotNull();
        assertThat(dto1.size()).isEqualTo(6);

        ResponseEntity<String> responseEntity100 =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/users/100/tasks", HttpMethod.GET);
        assertThat(responseEntity100.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getAllTasksOnProjectOnUser() {
        ResponseEntity<String> responseEntityProject3User2 =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/projects/3/users/2/tasks", HttpMethod.GET);
        assertThat(responseEntityProject3User2.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskDto> dto1 =
                (List<TaskDto>) getObjectFromString(responseEntityProject3User2.getBody(), new TypeReference<List<TaskDto>>() {
                });
        assertThat(dto1).isNotNull();
        assertThat(dto1.size()).isEqualTo(3);

        ResponseEntity<String> responseEntityProject100User2 =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/projects/100/users/2/tasks", HttpMethod.GET);
        assertThat(responseEntityProject100User2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ResponseEntity<String> responseEntityProject3User100 =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/projects/3/users/100/tasks", HttpMethod.GET);
        assertThat(responseEntityProject3User100.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ResponseEntity<String> responseEntityProject100User100 =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/projects/100/users/100/tasks", HttpMethod.GET);
        assertThat(responseEntityProject100User100.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void createOrUpdateTask() {
        //State before update
        ResponseEntity<String> responseEntityGetAllTasksBefore
                = getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/tasks", HttpMethod.GET);
        assertThat(responseEntityGetAllTasksBefore.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskDto> allTasksBefore =
                (List<TaskDto>) getObjectFromString(responseEntityGetAllTasksBefore.getBody(), new TypeReference<List<TaskDto>>() {
                });
        assertThat(allTasksBefore).isNotNull();
        assertThat(allTasksBefore.size()).isEqualTo(21);

        ResponseEntity<String> responseEntityGetProject1TasksBefore
                = getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/projects/1/tasks", HttpMethod.GET);
        assertThat(responseEntityGetProject1TasksBefore.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskDto> project1TasksBefore =
                (List<TaskDto>) getObjectFromString(responseEntityGetProject1TasksBefore.getBody(), new TypeReference<List<TaskDto>>() {
                });
        assertThat(project1TasksBefore).isNotNull();
        assertThat(project1TasksBefore.size()).isEqualTo(2);

        ResponseEntity<String> responseEntityGetUser1TasksBefore
                = getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/users/1/tasks", HttpMethod.GET);
        assertThat(responseEntityGetUser1TasksBefore.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskDto> user1TasksBefore =
                (List<TaskDto>) getObjectFromString(responseEntityGetUser1TasksBefore.getBody(), new TypeReference<List<TaskDto>>() {
                });
        assertThat(user1TasksBefore).isNotNull();
        assertThat(user1TasksBefore.size()).isEqualTo(6);

        TaskDto task20Before = allTasksBefore.stream().filter(taskDto -> taskDto.getId() == 20).findFirst().get();
        assertThat(task20Before).isNotNull();
        assertThat(task20Before.getName()).isEqualTo("Task20");
        assertThat(task20Before.getProjectId()).isEqualTo(5l);
        assertThat(task20Before.getUserId()).isEqualTo(2l);


        //Update
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", jwtTokenAdmin);
        String url = "http://localhost:" + port + "/api/v1/admin/tasks";

        //Valid case - Modify existing task
        task20Before.setName("Task20_Modified");
        task20Before.setUserId(1l);
        task20Before.setProjectId(1l);
        HttpEntity<TaskDto> requestModify = new HttpEntity<>(task20Before, httpHeaders);
        ResponseEntity<String> responseModify = restTemplate.postForEntity(url, requestModify, String.class);
        assertThat(responseModify.getStatusCode()).isEqualTo(HttpStatus.OK);

        //Invalid case - add new task without user without project
        TaskDto newTaskInvalidTask = new TaskDto();
        newTaskInvalidTask.setName("newTaskInvalidTask");
        newTaskInvalidTask.setDescription("newTaskInvalidTask");
        newTaskInvalidTask.setProjectId(null);
        newTaskInvalidTask.setUserId(null);
        newTaskInvalidTask.setEntityStatus(EntityStatus.ACTIVE);
        HttpEntity<TaskDto> requestInvalidTask = new HttpEntity<>(newTaskInvalidTask, httpHeaders);
        ResponseEntity<String> responseInvalidTask = restTemplate.postForEntity(url, requestInvalidTask, String.class);
        assertThat(responseInvalidTask.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        //Valid case - Add new task without user
        TaskDto newTaskWithoutUser = new TaskDto();
        newTaskWithoutUser.setName("newTaskWithoutUser");
        newTaskWithoutUser.setDescription("newTaskWithoutUser");
        newTaskWithoutUser.setProjectId(1l);
        newTaskWithoutUser.setUserId(null);
        newTaskWithoutUser.setEntityStatus(EntityStatus.ACTIVE);
        HttpEntity<TaskDto> requestNewTaskWithoutUser = new HttpEntity<>(newTaskWithoutUser, httpHeaders);
        ResponseEntity<String> responseNewTaskWithoutUser = restTemplate.postForEntity(url, requestNewTaskWithoutUser, String.class);
        assertThat(responseNewTaskWithoutUser.getStatusCode()).isEqualTo(HttpStatus.OK);

        //Valid case - Add new task with user
        TaskDto newTaskWithUser = new TaskDto();
        newTaskWithUser.setName("newTaskWithoutUser");
        newTaskWithUser.setDescription("newTaskWithoutUser");
        newTaskWithUser.setProjectId(1l);
        newTaskWithUser.setUserId(1l);
        newTaskWithUser.setEntityStatus(EntityStatus.ACTIVE);
        HttpEntity<TaskDto> requestNewTaskWithUser = new HttpEntity<>(newTaskWithUser, httpHeaders);
        ResponseEntity<String> responseNewTaskWithUser = restTemplate.postForEntity(url, requestNewTaskWithUser, String.class);
        assertThat(responseNewTaskWithUser.getStatusCode()).isEqualTo(HttpStatus.OK);


        //State after update
        ResponseEntity<String> responseEntityGetAllTasksAfter
                = getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/tasks", HttpMethod.GET);
        assertThat(responseEntityGetAllTasksAfter.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskDto> allTasksAfter =
                (List<TaskDto>) getObjectFromString(responseEntityGetAllTasksAfter.getBody(), new TypeReference<List<TaskDto>>() {
                });
        assertThat(allTasksAfter).isNotNull();
        //2 added, 1 modified, 1 invalid ignored
        assertThat(allTasksAfter.size()).isEqualTo(23);

        ResponseEntity<String> responseEntityGetProject1TasksAfter
                = getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/projects/1/tasks", HttpMethod.GET);
        assertThat(responseEntityGetProject1TasksAfter.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskDto> project1TasksAfter =
                (List<TaskDto>) getObjectFromString(responseEntityGetProject1TasksAfter.getBody(), new TypeReference<List<TaskDto>>() {
                });
        assertThat(project1TasksAfter).isNotNull();
        //there were 2, 2 added on project1, task20 moved from project5 to project1, total 5
        assertThat(project1TasksAfter.size()).isEqualTo(5);

        ResponseEntity<String> responseEntityGetUser1TasksAfter
                = getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/users/1/tasks", HttpMethod.GET);
        assertThat(responseEntityGetUser1TasksAfter.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskDto> user1TasksAfter =
                (List<TaskDto>) getObjectFromString(responseEntityGetUser1TasksAfter.getBody(), new TypeReference<List<TaskDto>>() {
                });
        assertThat(user1TasksAfter).isNotNull();
        //there were 6, 1 added on project1 on user1, task20 moved from project20 to project1 on user1, total 8
        assertThat(user1TasksAfter.size()).isEqualTo(8);

        TaskDto task20After = allTasksBefore.stream().filter(taskDto -> taskDto.getId() == 20).findFirst().get();
        assertThat(task20After).isNotNull();
        assertThat(task20After.getName()).isEqualTo("Task20_Modified");
        assertThat(task20After.getProjectId()).isEqualTo(1l);
        assertThat(task20After.getUserId()).isEqualTo(1l);
    }

    @Test
    public void deleteTaskById() {
        //State before delete
        ResponseEntity<String> responseEntityGetTask10Before =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/tasks/10", HttpMethod.GET);
        assertThat(responseEntityGetTask10Before.getStatusCode()).isEqualTo(HttpStatus.OK);
        TaskDto taskDto =
                (TaskDto) getObjectFromString(responseEntityGetTask10Before.getBody(), new TypeReference<TaskDto>() {
                });
        assertThat(taskDto).isNotNull();
        assertThat(taskDto.getProjectId()).isNotNull();
        assertThat(taskDto.getProjectId()).isEqualTo(3);
        assertThat(taskDto.getUserId()).isNotNull();
        assertThat(taskDto.getUserId()).isEqualTo(2);

        ResponseEntity<String> responseEntityUser2TasksBefore =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/users/2/tasks", HttpMethod.GET);
        assertThat(responseEntityUser2TasksBefore.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskDto> user2TasksBefore =
                (List<TaskDto>) getObjectFromString(responseEntityUser2TasksBefore.getBody(), new TypeReference<List<TaskDto>>() {
                });
        assertThat(user2TasksBefore.size()).isEqualTo(14);

        ResponseEntity<String> responseEntityProject3TasksBefore =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/projects/3/tasks", HttpMethod.GET);
        assertThat(responseEntityProject3TasksBefore.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskDto> project3TasksBefore =
                (List<TaskDto>) getObjectFromString(responseEntityProject3TasksBefore.getBody(), new TypeReference<List<TaskDto>>() {
                });
        assertThat(project3TasksBefore.size()).isEqualTo(6);


        //Delete task with id = 10
        ResponseEntity<String> responseEntityDeleteTask10 =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/tasks/10", HttpMethod.DELETE);
        assertThat(responseEntityGetTask10Before.getStatusCode()).isEqualTo(HttpStatus.OK);

        //State after delete
        ResponseEntity<String> responseEntityUser2TasksAfter =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/users/2/tasks", HttpMethod.GET);
        assertThat(responseEntityUser2TasksAfter.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskDto> user2TasksAfter =
                (List<TaskDto>) getObjectFromString(responseEntityUser2TasksAfter.getBody(), new TypeReference<List<TaskDto>>() {
                });
        assertThat(user2TasksAfter.size()).isEqualTo(13);

        ResponseEntity<String> responseEntityProject3TasksAfter =
                getResponseEntityWithValidAdminToken("http://localhost:" + port + "/api/v1/admin/projects/3/tasks", HttpMethod.GET);
        assertThat(responseEntityProject3TasksAfter.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskDto> project3TasksAfter =
                (List<TaskDto>) getObjectFromString(responseEntityProject3TasksAfter.getBody(), new TypeReference<List<TaskDto>>() {
                });
        assertThat(project3TasksAfter.size()).isEqualTo(5);
    }


    private ResponseEntity getResponseEntityWithValidAdminToken(String url, HttpMethod httpMethod) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", jwtTokenAdmin);
        HttpEntity requestEntity = new HttpEntity(httpHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, httpMethod, requestEntity, String.class);
        return responseEntity;
    }

    private Object getObjectFromString(String s, TypeReference typeRef) {
        try {
            return objectMapper.readValue(s, typeRef);
        } catch (Exception e) {
            return null;
        }
    }
}