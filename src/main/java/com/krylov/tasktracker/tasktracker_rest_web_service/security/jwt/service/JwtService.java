package com.krylov.tasktracker.tasktracker_rest_web_service.security.jwt.service;

import com.krylov.tasktracker.tasktracker_rest_web_service.entity.RoleEntity;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public interface JwtService {

    Optional<String>  createToken(String userName, List<RoleEntity> roles);

    Optional<String> resolveToken(HttpServletRequest httpServletRequest);

    boolean isValidToken(String token);

    Optional<Authentication> getAuthentication(String token);

}
