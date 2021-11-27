package com.krylov.tasktracker.tasktracker_rest_web_service.security.user.details.service;

import com.krylov.tasktracker.tasktracker_rest_web_service.entity.UserEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.security.user.details.UserDetailsFactory;
import com.krylov.tasktracker.tasktracker_rest_web_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        try {
            UserEntity userEntity = userService.findByName(userName);
            UserDetails result = UserDetailsFactory.createUserDetails(userEntity);
            return result;
        } catch (Exception e) {
            throw new UsernameNotFoundException("User with username: '" + userName + "' not found");
        }
    }
}
