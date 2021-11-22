package com.krylov.tasktracker.tasktracker_rest_web_service.controller;

import com.krylov.tasktracker.tasktracker_rest_web_service.dto.jwt.JwtDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.UserAuthenticationRequestDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.security.jwt.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/authentication")
public class AuthenticationRestController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationRestController(JwtService jwtService,
                                        AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("")
    public ResponseEntity authenticate(@RequestBody UserAuthenticationRequestDto userAuthenticationRequestDto) {
        String userName = userAuthenticationRequestDto.getUserName();
        String password = userAuthenticationRequestDto.getPassword();

        JwtDto result = jwtService.authenticateAndCreateToken(authenticationManager, userName, password);
        return new ResponseEntity(result, HttpStatus.OK);

    }


}
