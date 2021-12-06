package com.krylov.tasktracker.tasktracker_rest_web_service.controller;

import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.UserInfoDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.UserRegistrationRequestDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RegistrationRestControllerTest {

    @Autowired
    private RegistrationRestController registrationRestController;



    @Test
    void contextLoads() {
        System.out.println("Test1");
        assertThat(registrationRestController).isNotNull();
    }
//    @Test
//    void newUser() {
//        UserRegistrationRequestDto userRegistrationRequestDto = new UserRegistrationRequestDto();
//        userRegistrationRequestDto.setUserName("User1");
//        userRegistrationRequestDto.setFirstName("User1");
//        userRegistrationRequestDto.setLastName("User1");
//        userRegistrationRequestDto.setEmail("User1");
//        userRegistrationRequestDto.setPassword("User1");
//
//        UserInfoDto result = (UserInfoDto) registrationRestController.registerUser(userRegistrationRequestDto).getBody();
//        assertThat(result.getId()).isEqualTo(1L);
//    }












    @BeforeAll
    private static void setUp() {
        System.out.println("------------------BeforeAll");
    }

    @AfterAll
    private static void tearDown() {
        System.out.println("------------------AfterAll");
    }
}