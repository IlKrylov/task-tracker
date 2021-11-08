package com.krylov.tasktracker.tasktracker_rest_web_service.dto;

import lombok.Data;

@Data
public class ProjectDto {

    private Long id;
    private String name;
    private String description;

}
