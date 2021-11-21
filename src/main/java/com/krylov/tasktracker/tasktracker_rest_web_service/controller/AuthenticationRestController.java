package com.krylov.tasktracker.tasktracker_rest_web_service.controller;

import com.krylov.tasktracker.tasktracker_rest_web_service.controller.response.template.ResponseTemplate;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.jwt.JwtDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.UserAuthenticationRequestDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.UserEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.security.jwt.service.JwtService;
import com.krylov.tasktracker.tasktracker_rest_web_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/authentication")
public class AuthenticationRestController {

    private final JwtService jwtService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final ResponseTemplate responseTemplate;

    @Autowired
    public AuthenticationRestController(JwtService jwtService,
                                        UserService userService,
                                        AuthenticationManager authenticationManager,
                                        ResponseTemplate responseTemplate) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.responseTemplate = responseTemplate;
    }

    @PostMapping("")
    public ResponseEntity authenticate(@RequestBody UserAuthenticationRequestDto userAuthenticationRequestDto) {
        try {
            String userName = userAuthenticationRequestDto.getUserName();
            String password = userAuthenticationRequestDto.getPassword();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));
            UserEntity userEntity = userService.findByName(userName).orElseThrow();

            String token = jwtService.createToken(userName, userEntity.getRoles()).orElseThrow();
            JwtDto result = new JwtDto(userName, token);
            return responseTemplate.getResponseOk(result);

        } catch (AuthenticationException | NoSuchElementException e){
            return responseTemplate.getResponseBadRequest("Denied: invalid credentials");
        }
    }


}
