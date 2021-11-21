package com.krylov.tasktracker.tasktracker_rest_web_service.dto.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtDto {

    String userName;
    String token;

}
