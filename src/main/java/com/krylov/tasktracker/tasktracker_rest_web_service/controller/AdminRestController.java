package com.krylov.tasktracker.tasktracker_rest_web_service.controller;

import com.krylov.tasktracker.tasktracker_rest_web_service.controller.response.service.ResponseHandler;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.*;
import com.krylov.tasktracker.tasktracker_rest_web_service.service.*;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.ProjectEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.RoleEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.TaskEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminRestController {

    private UserService userService;
    private RoleService roleService;
    private ProjectService projectService;
    private TaskService taskService;
    private ResponseHandler responseHandler;

    private static final String BAD_REQUEST = "Bad request";
    private static final String DELETED_RESPONSE = "Selected object was deleted or was missing in Database";

    @Autowired
    public AdminRestController(UserService userService,
                               RoleService roleService,
                               ProjectService projectService,
                               TaskService taskService,
                               ResponseHandler responseHandler) {
        this.userService = userService;
        this.roleService = roleService;
        this.projectService = projectService;
        this.taskService = taskService;

        this.responseHandler = responseHandler;
    }

    // region Roles endpoints

    @GetMapping("/roles")
    public ResponseEntity getAllRoles() {

        List<RoleEntity> roleEntities = roleService.getAll();
        List<RoleDto> result = roleService.toDtoList(roleEntities);

        return responseHandler.getResponseOk(result);
    }

    @GetMapping("/users/{userId}/roles")
    public ResponseEntity getAllRolesOnUserById(@PathVariable Long userId) {
        Optional<List<RoleEntity>> optionalRoleEntityList = roleService.findAllUserRoles(userId);
        if (optionalRoleEntityList.isEmpty()) return responseHandler.getResponseBadRequest(BAD_REQUEST);

        List<RoleEntity> roleEntities = optionalRoleEntityList.get();
        List<RoleDto> result = roleService.toDtoList(roleEntities);

        return responseHandler.getResponseOk(result);
    }

    // endregion

    // region Users endpoints

    @GetMapping("/users")
    public ResponseEntity getAllUsers() {

        List<UserEntity> userEntities = userService.getAll();
        List<UserInfoResponseDto> result = userService.toDtoList(userEntities);

        return responseHandler.getResponseOk(result);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity getUserById(@PathVariable Long id) {

        Optional<UserEntity> entityOptional = userService.findById(id);
        if (entityOptional.isEmpty()) {
            return responseHandler.getResponseBadRequest(BAD_REQUEST);
        }
        UserEntity entity = entityOptional.get();
        UserInfoResponseDto result = userService.toDto(entity);

        return responseHandler.getResponseOk(result);
    }

    @GetMapping("/projects/{projectId}/users")
    public ResponseEntity getAllUsersOnProjectById(@PathVariable Long projectId) {
        Optional<List<UserEntity>> userEntitiesOptional = userService.findAllProjectUsers(projectId);
        if (userEntitiesOptional.isEmpty()) {
            return responseHandler.getResponseBadRequest(BAD_REQUEST);
        }
        List<UserEntity> userEntities = userEntitiesOptional.get();
        List<UserInfoResponseDto> result = userService.toDtoList(userEntities);

        return responseHandler.getResponseOk(result);
    }

    @PostMapping("/users")
    public ResponseEntity updateUserLinks(@RequestBody UserUpdateRequestDto updateRequestDto) {
        Optional<UserEntity> userEntitiesOptional = userService.updateLinks(updateRequestDto);
        if (userEntitiesOptional.isEmpty()) {
            return responseHandler.getResponseBadRequest(BAD_REQUEST);
        }
        UserInfoResponseDto result = userService.toDto(userEntitiesOptional.get());
        return responseHandler.getResponseOk(result);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity deleteUserById(@PathVariable Long id) {
        userService.deleteById(id);
        return responseHandler.getResponseOk(DELETED_RESPONSE);
    }

    // endregion

    // region Projects endpoints

    @GetMapping("/projects")
    public ResponseEntity getAllProjects() {

        List<ProjectEntity> projectEntities = projectService.getAll();
        List<ProjectDto> result = projectService.toDtoList(projectEntities);
        return responseHandler.getResponseOk(result);
    }

    @GetMapping("/projects/{id}")
    public ResponseEntity getProjectById(@PathVariable Long id) {

        Optional<ProjectEntity> entityOptional = projectService.findById(id);
        if (entityOptional.isEmpty()) {
            return responseHandler.getResponseBadRequest(BAD_REQUEST);
        }
        ProjectEntity entity = entityOptional.get();
        ProjectDto result = projectService.toDto(entity);

        return responseHandler.getResponseOk(result);
    }

    @GetMapping("/users/{userId}/projects")
    public ResponseEntity getAllProjectsOnUserById(@PathVariable Long userId) {
        Optional<List<ProjectEntity>> projectEntitiesOptional = projectService.findAllUserProjects(userId);
        if (projectEntitiesOptional.isEmpty()) {
            return responseHandler.getResponseBadRequest(BAD_REQUEST);
        }
        List<ProjectEntity> projectEntities = projectEntitiesOptional.get();
        List<ProjectDto> result = projectService.toDtoList(projectEntities);

        return responseHandler.getResponseOk(result);
    }

    @PostMapping("/projects")
    public ResponseEntity createOrUpdateProject(@RequestBody ProjectDto projectDto) {
        Optional<ProjectEntity> projectEntityOptional = projectService.saveOrUpdate(projectDto);
        if (projectEntityOptional.isEmpty()) return responseHandler.getResponseBadRequest(BAD_REQUEST);
        ProjectEntity entity = projectEntityOptional.get();
        ProjectDto result = projectService.toDto(entity);

        return responseHandler.getResponseOk(result);

    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity deleteProjectById(@PathVariable Long id) {
        projectService.deleteById(id);
        return responseHandler.getResponseOk(DELETED_RESPONSE);
    }

    // endregion

    // region Tasks endpoints

    @GetMapping("/tasks")
    public ResponseEntity getAllTasks() {

        List<TaskEntity> taskEntities = taskService.getAll();
        List<TaskDto> result = taskService.toDtoList(taskEntities);

        return responseHandler.getResponseOk(result);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity getTaskById(@PathVariable Long id) {

        Optional<TaskEntity> entityOptional = taskService.findById(id);
        if (entityOptional.isEmpty()) {
            return responseHandler.getResponseBadRequest(BAD_REQUEST);
        }
        TaskEntity entity = entityOptional.get();
        TaskDto result = taskService.toDto(entity);

        return responseHandler.getResponseOk(result);
    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity getAllTasksOnProjectById(@PathVariable Long projectId) {
        Optional<List<TaskEntity>> taskEntitiesOptional = taskService.findAllProjectTasks(projectId);
        if (taskEntitiesOptional.isEmpty()) {
            return responseHandler.getResponseBadRequest(BAD_REQUEST);
        }
        List<TaskEntity> taskEntities = taskEntitiesOptional.get();
        List<TaskDto> result = taskService.toDtoList(taskEntities);

        return responseHandler.getResponseOk(result);
    }

    @GetMapping("/users/{userId}/tasks")
    public ResponseEntity getAllTasksOnUserById(@PathVariable Long userId) {
        Optional<List<TaskEntity>> taskEntitiesOptional = taskService.findAllUserTasks(userId);
        if (taskEntitiesOptional.isEmpty()) {
            return responseHandler.getResponseBadRequest(BAD_REQUEST);
        }
        List<TaskEntity> taskEntities = taskEntitiesOptional.get();
        List<TaskDto> result = taskService.toDtoList(taskEntities);

        return responseHandler.getResponseOk(result);
    }

    @GetMapping("/projects/{projectId}/users/{userId}/tasks")
    public ResponseEntity getAllTasksOnProjectOnUser(@PathVariable Long projectId, @PathVariable Long userId) {
        Optional<List<TaskEntity>> taskEntitiesOptional = taskService.findAllProjectUserTasks(projectId, userId);
        if (taskEntitiesOptional.isEmpty()) {
            return responseHandler.getResponseBadRequest(BAD_REQUEST);
        }
        List<TaskEntity> taskEntities = taskEntitiesOptional.get();
        List<TaskDto> result = taskService.toDtoList(taskEntities);

        return responseHandler.getResponseOk(result);
    }

    @PostMapping("/tasks")
    public ResponseEntity createOrUpdateTask(@RequestBody TaskDto taskDto) {
        Optional<TaskEntity> taskEntityOptional = taskService.saveOrUpdate(taskDto);
        if (taskEntityOptional.isEmpty()) return responseHandler.getResponseBadRequest(BAD_REQUEST);
        TaskEntity entity = taskEntityOptional.get();
        TaskDto result = taskService.toDto(entity);

        return responseHandler.getResponseOk(result);

    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity deleteTaskById(@PathVariable Long id) {
        taskService.deleteById(id);
        return responseHandler.getResponseOk(DELETED_RESPONSE);
    }

    // endregion


}
