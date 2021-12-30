package com.krylov.tasktracker.tasktracker_rest_web_service.dto.task;

import com.krylov.tasktracker.tasktracker_rest_web_service.entity.enums.EntityStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskStatusChangeRequestDto {
    private Long id;
    private EntityStatus status;
}
