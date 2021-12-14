package com.krylov.tasktracker.tasktracker_rest_web_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.jwt.JwtDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.UserAuthenticationRequestDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.security.jwt.service.JwtService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class AuthenticationRestControllerTest {

    @LocalServerPort
    private int port;

    @Value("${root.admin.password}")
    private String adminPassword;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationRestController authenticationRestController;

    @BeforeAll
    private static void setUp(@Autowired DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("test-data.sql"));
        } catch (Exception e) {
        }
    }

    @Test
    public void contextLoads() {
        assertThat(authenticationRestController).isNotNull();
    }

    @Test
    public void authenticateAdmin() {
        String url = "http://localhost:" + port + "/api/v1/authentication";
        UserAuthenticationRequestDto authenticationRequestDto = new UserAuthenticationRequestDto("ADMIN", adminPassword);
        HttpEntity<UserAuthenticationRequestDto> request = new HttpEntity<>(authenticationRequestDto, new HttpHeaders());
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JwtDto jwtDto = null;
        try {
            jwtDto = objectMapper.readValue(response.getBody(), JwtDto.class);
        } catch (Exception e) {
        }
        assertThat(jwtDto).isNotNull();
        assertThat(jwtDto.getUserName()).isNotNull();
        assertThat(jwtDto.getToken()).isNotNull();
        assertThat(jwtDto.getToken().matches("^([\\w-]+\\.){2}[\\w-]+$")).isTrue();
        assertThat(jwtService.isValidToken(jwtDto.getToken())).isTrue();
    }

    @Test
    public void authenticateAdminWithBadPassword() {
        String url = "http://localhost:" + port + "/api/v1/authentication";
        UserAuthenticationRequestDto authenticationRequestDto = new UserAuthenticationRequestDto("ADMIN", "Unknown");
        HttpEntity<UserAuthenticationRequestDto> request = new HttpEntity<>(authenticationRequestDto, new HttpHeaders());
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo("Unable to authenticate and create JWT");
    }

    @Test
    public void authenticateUnknown() {
        String url = "http://localhost:" + port + "/api/v1/authentication";
        UserAuthenticationRequestDto authenticationRequestDto = new UserAuthenticationRequestDto("Unknown", "Unknown");
        HttpEntity<UserAuthenticationRequestDto> request = new HttpEntity<>(authenticationRequestDto, new HttpHeaders());
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo("Unable to authenticate and create JWT");
    }
}