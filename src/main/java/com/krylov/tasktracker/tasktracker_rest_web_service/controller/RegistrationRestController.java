package com.krylov.tasktracker.tasktracker_rest_web_service.controller;

import com.krylov.tasktracker.tasktracker_rest_web_service.controller.response.template.ResponseTemplate;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.UserInfoResponseDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.UserRegistrationRequestDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.UserEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/registration")
public class RegistrationRestController {

    private final UserService userService;
    private final ResponseTemplate responseTemplate;

    @Autowired
    public RegistrationRestController(UserService userService, ResponseTemplate responseTemplate) {
        this.userService = userService;
        this.responseTemplate = responseTemplate;
    }

    @PostMapping
    public ResponseEntity registerUser(@RequestBody UserRegistrationRequestDto userRegistrationRequestDto) {

        String userName = userRegistrationRequestDto.getUserName();
        String email = userRegistrationRequestDto.getEmail();

        if (userService.existsByUserName(userName)){
            return responseTemplate.getResponseBadRequest("Username '" + userName + "' already exists");
        }
        if (userService.existsByEmail(email)){
            return responseTemplate.getResponseBadRequest("Email '" + email + "' already exists");
        }

        Optional<UserEntity> optionalUserEntity = userService.register(userRegistrationRequestDto);
        if (optionalUserEntity.isEmpty()){
            return responseTemplate.getResponseBadRequest("Registration failed");
        }

        UserInfoResponseDto result = userService.toDto(optionalUserEntity.get());
        return responseTemplate.getResponseOk(result);
    }


}
