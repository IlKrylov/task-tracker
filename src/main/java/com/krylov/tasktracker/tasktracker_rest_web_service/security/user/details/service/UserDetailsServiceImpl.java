package com.krylov.tasktracker.tasktracker_rest_web_service.security.user.details.service;

import com.krylov.tasktracker.tasktracker_rest_web_service.entity.UserEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.security.user.details.UserDetailsFactory;
import com.krylov.tasktracker.tasktracker_rest_web_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        Optional<UserEntity> userEntityOptional = userService.findByName(userName);
        if (userEntityOptional.isEmpty()){
            throw new UsernameNotFoundException("User with username: '" + userName + "' not found");
        }

        UserDetails result = UserDetailsFactory.createUserDetails(userEntityOptional.get());
        return result;
    }
}
