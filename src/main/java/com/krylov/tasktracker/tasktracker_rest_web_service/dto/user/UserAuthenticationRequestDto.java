package com.krylov.tasktracker.tasktracker_rest_web_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserAuthenticationRequestDto {

    private String userName;
    private String password;

}
