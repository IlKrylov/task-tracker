package com.krylov.tasktracker.tasktracker_rest_web_service;

import com.krylov.tasktracker.tasktracker_rest_web_service.controller.AdminRestController;
import com.krylov.tasktracker.tasktracker_rest_web_service.controller.AuthenticationRestController;
import com.krylov.tasktracker.tasktracker_rest_web_service.controller.RegistrationRestController;
import com.krylov.tasktracker.tasktracker_rest_web_service.controller.UserRestController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TasktrackerRestWebServiceApplicationTests {

    @Autowired
    private AdminRestController adminRestController;
    @Autowired
    private UserRestController userRestController;
    @Autowired
    private AuthenticationRestController authenticationRestController;
    @Autowired
    private RegistrationRestController registrationRestController;


    @Test
    void contextLoads() {
        assertThat(adminRestController).isNotNull();
        assertThat(userRestController).isNotNull();
        assertThat(authenticationRestController).isNotNull();
        assertThat(registrationRestController).isNotNull();
    }

}
