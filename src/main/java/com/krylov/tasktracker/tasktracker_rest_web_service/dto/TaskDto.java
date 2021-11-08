package com.krylov.tasktracker.tasktracker_rest_web_service.dto;

import lombok.Data;

@Data
public class TaskDto {

    private Long id;
    private String name;
    private String description;
    private Long projectId;
    private String projectName;
    private Long userId;
    private String userName;

}
