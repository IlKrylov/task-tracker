package com.krylov.tasktracker.tasktracker_rest_web_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.UserInfoDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.UserRegistrationRequestDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegistrationRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RegistrationRestController registrationRestController;

    @BeforeAll
    private static void setUp(@Autowired DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("test-data.sql"));
        } catch (Exception e) {
        }
    }

    @Test
    public void contextLoads() {
        assertThat(registrationRestController).isNotNull();
    }

    @Test
    public void registerUserWithExistingUsername() {
        String url = "http://localhost:" + port + "/api/v1/registration";
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(
                "User2",
                "User5",
                "User5",
                "User5",
                "user5@user.ru"
        );
        HttpEntity<UserRegistrationRequestDto> request = new HttpEntity<>(requestDto, new HttpHeaders());
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("User with username='User2' already exists");
    }

    @Test
    public void registerUserWithExistingEmail() {
        String url = "http://localhost:" + port + "/api/v1/registration";
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(
                "User5",
                "User5",
                "User5",
                "User5",
                "user2@user.ru"
        );
        HttpEntity<UserRegistrationRequestDto> request = new HttpEntity<>(requestDto, new HttpHeaders());
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("User with email='user2@user.ru' already exists");
    }

    @Test
    public void registerNewUser() {
        String url = "http://localhost:" + port + "/api/v1/registration";
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(
                "User5",
                "User5",
                "User5",
                "User5",
                "user5@user.ru"
        );
        HttpEntity<UserRegistrationRequestDto> request = new HttpEntity<>(requestDto, new HttpHeaders());
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserInfoDto userInfoDto = null;
        try {
            userInfoDto = objectMapper.readValue(response.getBody(), UserInfoDto.class);
        } catch (Exception e) {
        }
        assertThat(userInfoDto).isNotNull();
        assertThat(userInfoDto.getId()).isNotNull();
        assertThat(userInfoDto.getId()).isEqualTo(5l);
    }
}