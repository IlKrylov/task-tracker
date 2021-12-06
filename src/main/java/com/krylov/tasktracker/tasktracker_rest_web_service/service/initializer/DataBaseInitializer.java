package com.krylov.tasktracker.tasktracker_rest_web_service.service.initializer;

import com.krylov.tasktracker.tasktracker_rest_web_service.entity.RoleEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.UserEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.enums.EntityStatus;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.enums.RoleType;
import com.krylov.tasktracker.tasktracker_rest_web_service.repository.RoleRepository;
import com.krylov.tasktracker.tasktracker_rest_web_service.repository.UserRepository;
import com.krylov.tasktracker.tasktracker_rest_web_service.security.encoder.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class DataBaseInitializer {

    @Value("${root.admin.password}")
    private String rootAdminPassword;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataBaseInitializer(UserRepository userRepository,
                               RoleRepository roleRepository,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @EventListener(ApplicationReadyEvent.class)
    public void InitDataBase() {
        initRole(RoleType.ROLE_ADMIN);
        initRole(RoleType.ROLE_USER);
        initRole(RoleType.ROLE_MANAGER);
        initRootAdmin();
    }


    private void initRole(RoleType roleType) {
        try {
            RoleEntity roleEntity = new RoleEntity();
            roleEntity.setName(roleType);
            roleEntity.setCreated(new Date());
            roleEntity.setUpdated(new Date());
            roleEntity.setStatus(EntityStatus.ACTIVE);
            roleRepository.save(roleEntity);
        } catch (Exception e) {
        }
    }

    private void initRootAdmin() {
        try {
            UserEntity userEntity = new UserEntity("ADMIN", "ADMIN", "ADMIN", "ADMIN",
                    passwordEncoder.encode(rootAdminPassword), List.of(roleRepository.findRoleAdmin().get()), null, null);
            userEntity.setCreated(new Date());
            userEntity.setUpdated(new Date());
            userEntity.setStatus(EntityStatus.ACTIVE);
            userRepository.save(userEntity);
        } catch (Exception e) {
        }
    }


}
