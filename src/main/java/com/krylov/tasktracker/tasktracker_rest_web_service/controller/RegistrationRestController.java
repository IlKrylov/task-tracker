package com.krylov.tasktracker.tasktracker_rest_web_service.controller;

import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.UserInfoDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.UserRegistrationRequestDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.UserEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/registration")
public class RegistrationRestController {

    private final UserService userService;

    @Autowired
    public RegistrationRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity registerUser(@RequestBody UserRegistrationRequestDto userRegistrationRequestDto) {
        UserEntity userEntity = userService.register(userRegistrationRequestDto);
        UserInfoDto result = userService.toDto(userEntity);
        return new ResponseEntity(result, HttpStatus.OK);
    }


}
