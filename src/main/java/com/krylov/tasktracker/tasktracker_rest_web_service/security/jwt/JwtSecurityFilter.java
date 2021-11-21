package com.krylov.tasktracker.tasktracker_rest_web_service.security.jwt;

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
import java.util.Optional;

@Component
public class JwtSecurityFilter extends GenericFilterBean {

    private final JwtService jwtService;

    @Autowired
    public JwtSecurityFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        Optional<String> optionalToken = jwtService.resolveToken((HttpServletRequest) servletRequest);

        if (optionalToken.isPresent() && jwtService.isValidToken(optionalToken.get())) {

            Optional<Authentication> optionalAuthentication = jwtService.getAuthentication(optionalToken.get());
            if (optionalAuthentication.isPresent()){
                SecurityContextHolder.getContext().setAuthentication(optionalAuthentication.get());

            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
