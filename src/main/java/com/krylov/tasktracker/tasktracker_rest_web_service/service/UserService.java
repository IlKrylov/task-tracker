package com.krylov.tasktracker.tasktracker_rest_web_service.service;

import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.UserInfoDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.UserRegistrationRequestDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.UserLinksUpdateRequestDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.UserEntity;

import java.util.List;

public interface UserService extends BaseEntityService<UserEntity, UserInfoDto> {

    UserEntity register(UserRegistrationRequestDto registrationRequestDto);

    List<String> updateLinks(UserLinksUpdateRequestDto updateRequestDto);

    List<UserEntity> findAllProjectUsers(Long projectId);

}
