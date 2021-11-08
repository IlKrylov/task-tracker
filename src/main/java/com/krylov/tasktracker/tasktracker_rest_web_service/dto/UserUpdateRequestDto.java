package com.krylov.tasktracker.tasktracker_rest_web_service.dto;

import com.krylov.tasktracker.tasktracker_rest_web_service.dto.enums.ChangeType;
import lombok.Data;

import java.util.Map;

@Data
public class UserUpdateRequestDto {

    private Long id;

    private Map<Long, ChangeType> roleChanges;
    private Map<Long, ChangeType> projectChanges;
    private Map<Long, ChangeType> taskChanges;
}
