package com.krylov.tasktracker.tasktracker_rest_web_service.security.user.details;

import com.krylov.tasktracker.tasktracker_rest_web_service.entity.RoleEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.UserEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.enums.EntityStatus;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public final class UserDetailsFactory {

    public static UserDetails createUserDetails(UserEntity userEntity) {
        UserDetailsImpl result = new UserDetailsImpl(
                userEntity.getId(),
                userEntity.getUserName(),
                userEntity.getFirsName(),
                userEntity.getLastName(),
                userEntity.getEmail(),
                userEntity.getPassword(),
                mapRolesToGrantedAuthorities(userEntity.getRoles()),
                true,
                true,
                true,
                userEntity.getStatus().equals(EntityStatus.ACTIVE)
        );
        return result;
    }

    private static List<GrantedAuthority> mapRolesToGrantedAuthorities(List<RoleEntity> roleEntityList) {
        List<GrantedAuthority> result =
                roleEntityList.stream()
                        .map(roleEntity -> new SimpleGrantedAuthority(roleEntity.getName().toString())).collect(Collectors.toList());
        return result;
    }


}
