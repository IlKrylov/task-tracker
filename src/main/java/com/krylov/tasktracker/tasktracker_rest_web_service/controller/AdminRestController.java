package com.krylov.tasktracker.tasktracker_rest_web_service.controller;

import com.krylov.tasktracker.tasktracker_rest_web_service.dto.project.ProjectDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.role.RoleDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.task.TaskDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.UserInfoDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.UserLinksUpdateRequestDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.ProjectEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.RoleEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.TaskEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.UserEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.service.ProjectService;
import com.krylov.tasktracker.tasktracker_rest_web_service.service.RoleService;
import com.krylov.tasktracker.tasktracker_rest_web_service.service.TaskService;
import com.krylov.tasktracker.tasktracker_rest_web_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminRestController {

    private final UserService userService;
    private final RoleService roleService;
    private final ProjectService projectService;
    private final TaskService taskService;

    @Autowired
    public AdminRestController(UserService userService,
                               RoleService roleService,
                               ProjectService projectService,
                               TaskService taskService) {
        this.userService = userService;
        this.roleService = roleService;
        this.projectService = projectService;
        this.taskService = taskService;
    }

    // region Roles endpoints

    @GetMapping("/roles")
    public ResponseEntity getAllRoles() {
        List<RoleEntity> roleEntities = roleService.getAll();
        List<RoleDto> result = roleService.toDtoList(roleEntities);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/roles")
    public ResponseEntity getAllRolesOnUserById(@PathVariable Long userId) {
        List<RoleEntity> roleEntities = roleService.findAllUserRoles(userId);
        List<RoleDto> result = roleService.toDtoList(roleEntities);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    // endregion

    // region Users endpoints

    @GetMapping("/users")
    public ResponseEntity getAllUsers() {
        List<UserEntity> userEntities = userService.getAll();
        List<UserInfoDto> result = userService.toDtoList(userEntities);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity getUserById(@PathVariable Long id) {
        UserEntity userEntity = userService.findById(id);
        UserInfoDto result = userService.toDto(userEntity);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @GetMapping("/projects/{projectId}/users")
    public ResponseEntity getAllUsersOnProjectById(@PathVariable Long projectId) {
        List<UserEntity> userEntities = userService.findAllProjectUsers(projectId);
        List<UserInfoDto> result = userService.toDtoList(userEntities);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @PostMapping("/users")
    public ResponseEntity updateUserLinks(@RequestBody UserLinksUpdateRequestDto userLinksUpdateRequestDto) {
        List<String> result = userService.updateLinks(userLinksUpdateRequestDto);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity deleteUserById(@PathVariable Long id) {
        userService.deleteById(id);
        String result = "User with id='" + id + "' was deleted";
        return new ResponseEntity(result, HttpStatus.OK);
    }

    // endregion

    // region Projects endpoints

    @GetMapping("/projects")
    public ResponseEntity getAllProjects() {
        List<ProjectEntity> projectEntities = projectService.getAll();
        List<ProjectDto> result = projectService.toDtoList(projectEntities);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @GetMapping("/projects/{id}")
    public ResponseEntity getProjectById(@PathVariable Long id) {
        ProjectEntity projectEntity = projectService.findById(id);
        ProjectDto result = projectService.toDto(projectEntity);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/projects")
    public ResponseEntity getAllProjectsOnUserById(@PathVariable Long userId) {
        List<ProjectEntity> projectEntities = projectService.findAllUserProjects(userId);
        List<ProjectDto> result = projectService.toDtoList(projectEntities);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @PostMapping("/projects")
    public ResponseEntity createOrUpdateProject(@RequestBody ProjectDto projectDto) {
        ProjectEntity projectEntity = projectService.saveOrUpdate(projectDto);
        ProjectDto result = projectService.toDto(projectEntity);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity deleteProjectById(@PathVariable Long id) {
        projectService.deleteById(id);
        String result = "Project with id='" + id + "' was deleted";
        return new ResponseEntity(result, HttpStatus.OK);
    }

    // endregion

    // region Tasks endpoints

    @GetMapping("/tasks")
    public ResponseEntity getAllTasks() {
        List<TaskEntity> taskEntities = taskService.getAll();
        List<TaskDto> result = taskService.toDtoList(taskEntities);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity getTaskById(@PathVariable Long id) {
        TaskEntity taskEntity = taskService.findById(id);
        TaskDto result = taskService.toDto(taskEntity);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity getAllTasksOnProjectById(@PathVariable Long projectId) {
        List<TaskEntity> taskEntities = taskService.findAllProjectTasks(projectId);
        List<TaskDto> result = taskService.toDtoList(taskEntities);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/tasks")
    public ResponseEntity getAllTasksOnUserById(@PathVariable Long userId) {
        List<TaskEntity> taskEntities = taskService.findAllUserTasks(userId);
        List<TaskDto> result = taskService.toDtoList(taskEntities);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @GetMapping("/projects/{projectId}/users/{userId}/tasks")
    public ResponseEntity getAllTasksOnProjectOnUser(@PathVariable Long projectId, @PathVariable Long userId) {
        List<TaskEntity> taskEntities = taskService.findAllProjectUserTasks(projectId, userId);
        List<TaskDto> result = taskService.toDtoList(taskEntities);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @PostMapping("/tasks")
    public ResponseEntity createOrUpdateTask(@RequestBody TaskDto taskDto) {
        TaskEntity taskEntity = taskService.saveOrUpdate(taskDto);
        TaskDto result = taskService.toDto(taskEntity);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity deleteTaskById(@PathVariable Long id) {
        taskService.deleteById(id);
        String result = "Task with id='" + id + "' was deleted";
        return new ResponseEntity(result, HttpStatus.OK);
    }

    // endregion


}
