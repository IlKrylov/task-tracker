package com.krylov.tasktracker.tasktracker_rest_web_service.dto;

import com.krylov.tasktracker.tasktracker_rest_web_service.dto.enums.ChangeType;
import lombok.Data;

import java.util.Map;

@Data
public class UserInfoResponseDto {

    private Long id;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;

}
