package com.krylov.tasktracker.tasktracker_rest_web_service.security.jwt.service;

import com.krylov.tasktracker.tasktracker_rest_web_service.dto.jwt.JwtDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public interface JwtService {

    JwtDto authenticateAndCreateToken(AuthenticationManager authenticationManager, String userName, String password);

    String resolveToken(HttpServletRequest httpServletRequest);

    boolean isValidToken(String token);

    Authentication getAuthentication(String token);

}
