package com.krylov.tasktracker.tasktracker_rest_web_service.dto.task;

import com.krylov.tasktracker.tasktracker_rest_web_service.entity.enums.EntityStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class TaskDto {

    private Long id;
    private String name;
    private String description;
    private Long projectId;
    private String projectName;
    private Long userId;
    private String userName;
    private EntityStatus entityStatus;

}
