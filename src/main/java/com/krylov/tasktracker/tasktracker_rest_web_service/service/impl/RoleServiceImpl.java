package com.krylov.tasktracker.tasktracker_rest_web_service.service.impl;

import com.krylov.tasktracker.tasktracker_rest_web_service.dto.RoleDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.ProjectEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.RoleEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.UserEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.repository.RoleRepository;
import com.krylov.tasktracker.tasktracker_rest_web_service.repository.UserRepository;
import com.krylov.tasktracker.tasktracker_rest_web_service.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository,
                           UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public List<RoleEntity> getAll() {
        List<RoleEntity> result = roleRepository.findAll();
        return result;
    }

    @Override
    @Transactional
    public Optional<RoleEntity> findById(Long id) {
        RoleEntity result = roleRepository.findById(id).orElse(null);
        return Optional.ofNullable(result);
    }

    @Override
    @Transactional
    public Optional<RoleEntity>  findByName(String name) {
        RoleEntity result = roleRepository.findByName(name);
        return Optional.ofNullable(result);
    }

    @Override
    @Transactional
    public Optional<List<RoleEntity>> findAllUserRoles(Long userId) {
        Optional<UserEntity> userEntityOptional = userRepository.findById(userId);
        if (userEntityOptional.isEmpty()) return Optional.empty();
        UserEntity userEntity = userEntityOptional.get();
        List<RoleEntity> result = userEntity.getRoles();
        return Optional.ofNullable(result);
    }

    @Override
    @Transactional
    public Optional<RoleEntity>  findRoleUser() {
        return roleRepository.findRoleUser();
    }

    @Override
    @Transactional
    public Optional<RoleEntity>  findRoleAdmin() {
        return roleRepository.findRoleAdmin();
    }

    @Override
    @Transactional
    public Optional<RoleEntity> findRoleManager() {
        return roleRepository.findRoleManager();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        roleRepository.deleteById(id);
    }

    @Override
    @Transactional
    public boolean existsById(Long id) {
        return roleRepository.existsById(id);
    }

    @Override
    @Transactional
    public Optional<RoleEntity> toEntity(RoleDto dto) {
        if (dto == null) return Optional.empty();
        Optional<RoleEntity> result = roleRepository.findById(dto.getId());
        return result;
    }

    @Override
    public RoleDto toDto(RoleEntity entity) {
        RoleDto result = new RoleDto();
        result.setId(entity.getId());
        result.setName(entity.getName());
        return result;
    }

    @Override
    public List<RoleDto> toDtoList(List<RoleEntity> entityList) {
        List<RoleDto> result =
                entityList.stream().map(roleEntity -> toDto(roleEntity)).collect(Collectors.toList());
        return result;
    }
}
