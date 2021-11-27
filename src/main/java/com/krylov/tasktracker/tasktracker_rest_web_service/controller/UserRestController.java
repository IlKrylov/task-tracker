package com.krylov.tasktracker.tasktracker_rest_web_service.controller;

import com.krylov.tasktracker.tasktracker_rest_web_service.dto.project.ProjectDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.task.TaskDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.task.TaskStatusChangeRequestDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.ProjectEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.TaskEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.security.user.details.UserDetailsImpl;
import com.krylov.tasktracker.tasktracker_rest_web_service.service.ProjectService;
import com.krylov.tasktracker.tasktracker_rest_web_service.service.TaskService;
import com.krylov.tasktracker.tasktracker_rest_web_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserRestController {

    private final UserService userService;
    private final ProjectService projectService;
    private final TaskService taskService;

    @Autowired
    public UserRestController(UserService userService,
                              ProjectService projectService,
                              TaskService taskService) {
        this.userService = userService;
        this.projectService = projectService;
        this.taskService = taskService;
    }

    // region Projects endpoints

    @GetMapping("/projects")
    public ResponseEntity getUserProjects(Authentication authentication) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        List<ProjectEntity> projectEntities = projectService.findAllUserProjects(userId);
        List<ProjectDto> result = projectService.toDtoList(projectEntities);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    // endregion

    // region Task endpoints

    @GetMapping("/tasks")
    public ResponseEntity getUserTasks(Authentication authentication) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        List<TaskEntity> taskEntities = taskService.findAllUserTasks(userId);
        List<TaskDto> result = taskService.toDtoList(taskEntities);
        return new ResponseEntity(result, HttpStatus.OK);
    }


    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity getAllUserTasksOnProject(Authentication authentication, @PathVariable Long projectId) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        List<TaskEntity> taskEntities = taskService.findAllProjectUserTasks(projectId, userId);
        List<TaskDto> result = taskService.toDtoList(taskEntities);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @PostMapping("/tasks")
    public ResponseEntity updateTask(Authentication authentication, @RequestBody TaskStatusChangeRequestDto inputDto) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        TaskEntity taskEntity = taskService.checkAccessAndSaveOrUpdateStatus(userId, inputDto);
        TaskDto result = taskService.toDto(taskEntity);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    // endregion

}
