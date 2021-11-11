package com.krylov.tasktracker.tasktracker_rest_web_service.dto.user;

import lombok.Data;

@Data
public class UserInfoResponseDto {

    private Long id;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;

}
