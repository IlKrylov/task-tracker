package com.krylov.tasktracker.tasktracker_rest_web_service.dto.role;

import com.krylov.tasktracker.tasktracker_rest_web_service.entity.enums.RoleType;
import lombok.Data;

@Data
public class RoleDto {

    Long id;

    RoleType name;
    
}
