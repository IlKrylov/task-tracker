package com.krylov.tasktracker.tasktracker_rest_web_service.service;

import com.krylov.tasktracker.tasktracker_rest_web_service.dto.UserInfoResponseDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.UserRegistrationRequestDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.UserUpdateRequestDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserService extends BaseEntityService<UserEntity, UserInfoResponseDto> {

    Optional<UserEntity> register(UserRegistrationRequestDto registrationRequestDto);

    Optional<UserEntity> updateLinks(UserUpdateRequestDto updateRequestDto);

    Optional<List<UserEntity>> findAllProjectUsers(Long projectId);

}
