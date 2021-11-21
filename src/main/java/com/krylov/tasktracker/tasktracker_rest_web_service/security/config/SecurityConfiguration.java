package com.krylov.tasktracker.tasktracker_rest_web_service.security.config;

import com.krylov.tasktracker.tasktracker_rest_web_service.security.jwt.JwtSecurityConfigureAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String REGISTRATION_ENDPOINT = "/api/v1/registration";
    private static final String AUTHENTICATION_ENDPOINT = "/api/v1/authentication";
    private static final String ADMIN_ENDPOINTS = "/api/v1/admin/**";
    private static final String USER_ENDPOINTS = "/api/v1/user/**";
    private static final String MANAGER_ENDPOINTS = "/api/v1/manager/**";

    private final JwtSecurityConfigureAdapter jwtSecurityConfigureAdapter;

    @Autowired
    public SecurityConfiguration(JwtSecurityConfigureAdapter jwtSecurityConfigureAdapter) {
        this.jwtSecurityConfigureAdapter = jwtSecurityConfigureAdapter;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity

//                .formLogin()
//                .and()
//                .logout()
//                .and()
//                .httpBasic()
//                .and()

                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(REGISTRATION_ENDPOINT).permitAll()
                .antMatchers(AUTHENTICATION_ENDPOINT).permitAll()
                .antMatchers(ADMIN_ENDPOINTS).hasRole("ADMIN")
                .antMatchers(USER_ENDPOINTS).hasRole("USER")
                .antMatchers(MANAGER_ENDPOINTS).hasRole("MANAGER")
                .anyRequest().authenticated()
                .and()
                .apply(jwtSecurityConfigureAdapter);
    }
}
