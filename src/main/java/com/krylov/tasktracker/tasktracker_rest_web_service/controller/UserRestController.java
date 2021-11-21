package com.krylov.tasktracker.tasktracker_rest_web_service.controller;

import com.krylov.tasktracker.tasktracker_rest_web_service.controller.response.template.ResponseTemplate;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/user")
public class UserRestController {

    private final UserService userService;
    private final ProjectService projectService;
    private final TaskService taskService;
    private final ResponseTemplate responseTemplate;

    private static final String BAD_REQUEST = "Bad request";

    @Autowired
    public UserRestController(UserService userService,
                              ProjectService projectService,
                              TaskService taskService,
                              ResponseTemplate responseTemplate) {
        this.userService = userService;
        this.projectService = projectService;
        this.taskService = taskService;
        this.responseTemplate = responseTemplate;
    }

    // region Projects endpoints

    @GetMapping("/projects")
    public ResponseEntity getUserProjects(Authentication authentication) {
        Long userId = ((UserDetailsImpl)authentication.getPrincipal()).getId();
        Optional<List<ProjectEntity>> optionalProjectEntities = projectService.findAllUserProjects(userId);
        if (optionalProjectEntities.isEmpty()) {
            return responseTemplate.getResponseBadRequest(BAD_REQUEST + ": User's project list is empty");
        }
        List<ProjectDto> result = projectService.toDtoList(optionalProjectEntities.get());
        return responseTemplate.getResponseOk(result);
    }

    // endregion

    // region Task endpoints

    @GetMapping("/tasks")
    public ResponseEntity getUserTasks(Authentication authentication) {
        Long userId = ((UserDetailsImpl)authentication.getPrincipal()).getId();
        Optional<List<TaskEntity>> optionalTaskEntities = taskService.findAllUserTasks(userId);
        if (optionalTaskEntities.isEmpty()) {
            return responseTemplate.getResponseBadRequest(BAD_REQUEST + ": User's task list is empty");
        }
        List<TaskDto> result = taskService.toDtoList(optionalTaskEntities.get());
        return responseTemplate.getResponseOk(result);
    }

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity getTaskById(Authentication authentication, @PathVariable Long taskId) {
        Long userId = ((UserDetailsImpl)authentication.getPrincipal()).getId();
        Optional<List<TaskEntity>> optionalTaskEntities = taskService.findAllUserTasks(userId);
        if (optionalTaskEntities.isEmpty()) {
            return responseTemplate.getResponseBadRequest(BAD_REQUEST + ": User's task list is empty");
        }
        List<TaskEntity> allUserTasks = optionalTaskEntities.get();
        Optional<TaskEntity> optionalTaskEntity = allUserTasks.stream().filter(taskEntity -> taskEntity.getId() == taskId).findFirst();
        if (optionalTaskEntity.isEmpty()){
            return responseTemplate.getResponseBadRequest(BAD_REQUEST + ": User's task list has no task with id=" + taskId);
        }
        TaskDto result = taskService.toDto(optionalTaskEntity.get());
        return responseTemplate.getResponseOk(result);
    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity getAllUserTasksOnProject(Authentication authentication, @PathVariable Long projectId){
        Long userId = ((UserDetailsImpl)authentication.getPrincipal()).getId();
        Optional<List<TaskEntity>> optionalTaskEntities = taskService.findAllUserTasks(userId);
        if (optionalTaskEntities.isEmpty()) {
            return responseTemplate.getResponseBadRequest(BAD_REQUEST + ": User's task list is empty");
        }
        List<TaskEntity> allUserTasks = optionalTaskEntities.get();
        List<TaskEntity> taskEntities = allUserTasks.stream()
                .filter(taskEntity -> taskEntity.getProject().getId() == projectId).collect(Collectors.toList());
        List<TaskDto> result = taskService.toDtoList(taskEntities);
        return responseTemplate.getResponseOk(result);
    }

    @PostMapping("/tasks")
    public ResponseEntity updateTask(Authentication authentication, @RequestBody TaskStatusChangeRequestDto inputDto){
        Long userId = ((UserDetailsImpl)authentication.getPrincipal()).getId();
        Optional<List<TaskEntity>> optionalTaskEntities = taskService.findAllUserTasks(userId);
        if (optionalTaskEntities.isEmpty()) {
            return responseTemplate.getResponseBadRequest(BAD_REQUEST + ": User's task list is empty");
        }
        List<TaskEntity> allUserTasks = optionalTaskEntities.get();
        Optional<TaskEntity> optionalTaskEntity = allUserTasks.stream().filter(taskEntity -> taskEntity.getId() == inputDto.getId()).findFirst();
        if (optionalTaskEntity.isEmpty()){
            return responseTemplate.getResponseBadRequest(BAD_REQUEST + ": User's task list has no task with id=" + inputDto.getId());
        }
        TaskEntity originalTaskEntity = optionalTaskEntity.get();
        TaskDto taskDto = taskService.toDto(originalTaskEntity);
        taskDto.setEntityStatus(inputDto.getStatus());

        taskService.saveOrUpdate(taskDto);
        return responseTemplate.getResponseOk(taskDto);
    }

    // endregion






}
