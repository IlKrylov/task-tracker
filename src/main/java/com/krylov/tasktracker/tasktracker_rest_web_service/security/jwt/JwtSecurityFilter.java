package com.krylov.tasktracker.tasktracker_rest_web_service.security.jwt;

import com.krylov.tasktracker.tasktracker_rest_web_service.exception.JwtException;
import com.krylov.tasktracker.tasktracker_rest_web_service.security.jwt.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class JwtSecurityFilter extends GenericFilterBean {

    private final JwtService jwtService;

    @Autowired
    public JwtSecurityFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            String token = jwtService.resolveToken((HttpServletRequest) servletRequest);
            if (jwtService.isValidToken(token)) {
                Authentication authentication = jwtService.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException e) {

        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
