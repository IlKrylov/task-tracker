package com.krylov.tasktracker.tasktracker_rest_web_service.dto.task;

import com.krylov.tasktracker.tasktracker_rest_web_service.entity.enums.EntityStatus;
import lombok.Data;

@Data
public class TaskStatusChangeRequestDto {
    private Long id;
    private EntityStatus status;
}
