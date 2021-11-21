package com.krylov.tasktracker.tasktracker_rest_web_service.controller;

import com.krylov.tasktracker.tasktracker_rest_web_service.controller.response.template.ResponseTemplate;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.project.ProjectDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.role.RoleDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.task.TaskDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.UserInfoResponseDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.UserLinksUpdateRequestDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.enums.ChangeType;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.ProjectEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.RoleEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.TaskEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.UserEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.service.ProjectService;
import com.krylov.tasktracker.tasktracker_rest_web_service.service.RoleService;
import com.krylov.tasktracker.tasktracker_rest_web_service.service.TaskService;
import com.krylov.tasktracker.tasktracker_rest_web_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminRestController {

    private final UserService userService;
    private final RoleService roleService;
    private final ProjectService projectService;
    private final TaskService taskService;
    private final ResponseTemplate responseTemplate;

    private static final String BAD_REQUEST = "Bad request";
    private static final String DELETED_RESPONSE = "Selected object was deleted or was missing in Database";

    @Autowired
    public AdminRestController(UserService userService,
                               RoleService roleService,
                               ProjectService projectService,
                               TaskService taskService,
                               ResponseTemplate responseTemplate) {
        this.userService = userService;
        this.roleService = roleService;
        this.projectService = projectService;
        this.taskService = taskService;

        this.responseTemplate = responseTemplate;
    }

    // region Roles endpoints

    @GetMapping("/roles")
    public ResponseEntity getAllRoles() {

        List<RoleEntity> roleEntities = roleService.getAll();
        List<RoleDto> result = roleService.toDtoList(roleEntities);

        return responseTemplate.getResponseOk(result);
    }

    @GetMapping("/users/{userId}/roles")
    public ResponseEntity getAllRolesOnUserById(@PathVariable Long userId) {
        Optional<List<RoleEntity>> optionalRoleEntityList = roleService.findAllUserRoles(userId);
        if (optionalRoleEntityList.isEmpty()) return responseTemplate.getResponseBadRequest(BAD_REQUEST);

        List<RoleEntity> roleEntities = optionalRoleEntityList.get();
        List<RoleDto> result = roleService.toDtoList(roleEntities);

        return responseTemplate.getResponseOk(result);
    }

    // endregion

    // region Users endpoints

    @GetMapping("/users")
    public ResponseEntity getAllUsers() {

        List<UserEntity> userEntities = userService.getAll();
        List<UserInfoResponseDto> result = userService.toDtoList(userEntities);

        return responseTemplate.getResponseOk(result);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity getUserById(@PathVariable Long id) {

        Optional<UserEntity> entityOptional = userService.findById(id);
        if (entityOptional.isEmpty()) {
            return responseTemplate.getResponseBadRequest(BAD_REQUEST);
        }
        UserEntity entity = entityOptional.get();
        UserInfoResponseDto result = userService.toDto(entity);

        return responseTemplate.getResponseOk(result);
    }

    @GetMapping("/projects/{projectId}/users")
    public ResponseEntity getAllUsersOnProjectById(@PathVariable Long projectId) {
        Optional<List<UserEntity>> userEntitiesOptional = userService.findAllProjectUsers(projectId);
        if (userEntitiesOptional.isEmpty()) {
            return responseTemplate.getResponseBadRequest(BAD_REQUEST);
        }
        List<UserEntity> userEntities = userEntitiesOptional.get();
        List<UserInfoResponseDto> result = userService.toDtoList(userEntities);

        return responseTemplate.getResponseOk(result);
    }

    @PostMapping("/users")
    public ResponseEntity updateUserLinks(@RequestBody UserLinksUpdateRequestDto userLinksUpdateRequestDto) {
        Optional<List<String>> optionalChangeLog = userService.updateLinks(userLinksUpdateRequestDto);
        if (optionalChangeLog.isEmpty()) {
            return responseTemplate.getResponseBadRequest(BAD_REQUEST);
        }
        return responseTemplate.getResponseOk(optionalChangeLog.get());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity deleteUserById(@PathVariable Long id) {
        userService.deleteById(id);
        return responseTemplate.getResponseOk(DELETED_RESPONSE);
    }

    // endregion

    // region Projects endpoints

    @GetMapping("/projects")
    public ResponseEntity getAllProjects() {

        List<ProjectEntity> projectEntities = projectService.getAll();
        List<ProjectDto> result = projectService.toDtoList(projectEntities);
        return responseTemplate.getResponseOk(result);
    }

    @GetMapping("/projects/{id}")
    public ResponseEntity getProjectById(@PathVariable Long id) {

        Optional<ProjectEntity> entityOptional = projectService.findById(id);
        if (entityOptional.isEmpty()) {
            return responseTemplate.getResponseBadRequest(BAD_REQUEST);
        }
        ProjectEntity entity = entityOptional.get();
        ProjectDto result = projectService.toDto(entity);

        return responseTemplate.getResponseOk(result);
    }

    @GetMapping("/users/{userId}/projects")
    public ResponseEntity getAllProjectsOnUserById(@PathVariable Long userId) {
        Optional<List<ProjectEntity>> projectEntitiesOptional = projectService.findAllUserProjects(userId);
        if (projectEntitiesOptional.isEmpty()) {
            return responseTemplate.getResponseBadRequest(BAD_REQUEST);
        }
        List<ProjectEntity> projectEntities = projectEntitiesOptional.get();
        List<ProjectDto> result = projectService.toDtoList(projectEntities);

        return responseTemplate.getResponseOk(result);
    }

    @PostMapping("/projects")
    public ResponseEntity createOrUpdateProject(@RequestBody ProjectDto projectDto) {
        try{
            Optional<ProjectEntity> projectEntityOptional = projectService.saveOrUpdate(projectDto);
            if (projectEntityOptional.isEmpty()) return responseTemplate.getResponseBadRequest(BAD_REQUEST);
            ProjectEntity entity = projectEntityOptional.get();
            ProjectDto result = projectService.toDto(entity);

            return responseTemplate.getResponseOk(result);

        } catch (Exception e){
            return responseTemplate.getResponseBadRequest("DataBase constraint exception");
        }
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity deleteProjectById(@PathVariable Long id) {
        projectService.deleteById(id);
        return responseTemplate.getResponseOk(DELETED_RESPONSE);
    }

    // endregion

    // region Tasks endpoints

    @GetMapping("/tasks")
    public ResponseEntity getAllTasks() {

        List<TaskEntity> taskEntities = taskService.getAll();
        List<TaskDto> result = taskService.toDtoList(taskEntities);

        return responseTemplate.getResponseOk(result);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity getTaskById(@PathVariable Long id) {

        Optional<TaskEntity> entityOptional = taskService.findById(id);
        if (entityOptional.isEmpty()) {
            return responseTemplate.getResponseBadRequest(BAD_REQUEST);
        }
        TaskEntity entity = entityOptional.get();
        TaskDto result = taskService.toDto(entity);

        return responseTemplate.getResponseOk(result);
    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity getAllTasksOnProjectById(@PathVariable Long projectId) {
        Optional<List<TaskEntity>> taskEntitiesOptional = taskService.findAllProjectTasks(projectId);
        if (taskEntitiesOptional.isEmpty()) {
            return responseTemplate.getResponseBadRequest(BAD_REQUEST);
        }
        List<TaskEntity> taskEntities = taskEntitiesOptional.get();
        List<TaskDto> result = taskService.toDtoList(taskEntities);

        return responseTemplate.getResponseOk(result);
    }

    @GetMapping("/users/{userId}/tasks")
    public ResponseEntity getAllTasksOnUserById(@PathVariable Long userId) {
        Optional<List<TaskEntity>> taskEntitiesOptional = taskService.findAllUserTasks(userId);
        if (taskEntitiesOptional.isEmpty()) {
            return responseTemplate.getResponseBadRequest(BAD_REQUEST);
        }
        List<TaskEntity> taskEntities = taskEntitiesOptional.get();
        List<TaskDto> result = taskService.toDtoList(taskEntities);

        return responseTemplate.getResponseOk(result);
    }

    @GetMapping("/projects/{projectId}/users/{userId}/tasks")
    public ResponseEntity getAllTasksOnProjectOnUser(@PathVariable Long projectId, @PathVariable Long userId) {
        Optional<List<TaskEntity>> taskEntitiesOptional = taskService.findAllProjectUserTasks(projectId, userId);
        if (taskEntitiesOptional.isEmpty()) {
            return responseTemplate.getResponseBadRequest(BAD_REQUEST);
        }
        List<TaskEntity> taskEntities = taskEntitiesOptional.get();
        List<TaskDto> result = taskService.toDtoList(taskEntities);

        return responseTemplate.getResponseOk(result);
    }

    @PostMapping("/tasks")
    public ResponseEntity createOrUpdateTask(@RequestBody TaskDto taskDto) {
        try{
            Optional<TaskEntity> taskEntityOptional = taskService.saveOrUpdate(taskDto);
            if (taskEntityOptional.isEmpty()) return responseTemplate.getResponseBadRequest(BAD_REQUEST);
            TaskEntity entity = taskEntityOptional.get();
            TaskDto result = taskService.toDto(entity);

            return responseTemplate.getResponseOk(result);

        } catch (Exception e){
            return responseTemplate.getResponseBadRequest("DataBase constraint exception");
        }
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity deleteTaskById(@PathVariable Long id) {
        taskService.deleteById(id);
        return responseTemplate.getResponseOk(DELETED_RESPONSE);
    }

    // endregion


}
