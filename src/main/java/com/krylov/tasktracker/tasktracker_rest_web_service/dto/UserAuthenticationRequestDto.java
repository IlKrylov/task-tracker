package com.krylov.tasktracker.tasktracker_rest_web_service.dto;

import lombok.Data;

@Data
public class UserAuthenticationRequestDto {

    private String userName;
    private String password;

}
