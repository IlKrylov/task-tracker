package com.krylov.tasktracker.tasktracker_rest_web_service.service.impl;

import com.krylov.tasktracker.tasktracker_rest_web_service.dto.role.RoleDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.RoleEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.UserEntity;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.enums.EntityType;
import com.krylov.tasktracker.tasktracker_rest_web_service.exception.DataBaseUpdateException;
import com.krylov.tasktracker.tasktracker_rest_web_service.exception.InvalidDtoException;
import com.krylov.tasktracker.tasktracker_rest_web_service.exception.NoSuchElementExceptionFactory;
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
    private final NoSuchElementExceptionFactory noSuchElementExceptionFactory;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository,
                           UserRepository userRepository,
                           NoSuchElementExceptionFactory noSuchElementExceptionFactory) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.noSuchElementExceptionFactory = noSuchElementExceptionFactory;
    }

    @Override
    @Transactional
    public List<RoleEntity> getAll() {
        List<RoleEntity> result = roleRepository.findAll();
        return result;
    }

    @Override
    @Transactional
    public RoleEntity findById(Long id) {
        RoleEntity result = roleRepository.findById(id)
                .orElseThrow(()-> noSuchElementExceptionFactory.getNoSuchElementException(EntityType.ROLE, "id", id));
        return result;
    }

    @Override
    @Transactional
    public RoleEntity  findByName(String name) {
        RoleEntity result = roleRepository.findByName(name);
        if (result == null) throw noSuchElementExceptionFactory.getNoSuchElementException(EntityType.ROLE, "name", name);
        return result;
    }

    @Override
    @Transactional
    public List<RoleEntity> findAllUserRoles(Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> noSuchElementExceptionFactory.getNoSuchElementException(EntityType.USER, "id", userId));
        List<RoleEntity> result = userEntity.getRoles();
        return result;
    }

    @Override
    @Transactional
    public RoleEntity findRoleUser() {
        RoleEntity result = roleRepository.findRoleUser()
                .orElseThrow(() -> noSuchElementExceptionFactory.getNoSuchElementException(EntityType.ROLE, "name", "ROLE_USER"));
        return result;
    }

    @Override
    @Transactional
    public RoleEntity  findRoleAdmin() {
        RoleEntity result = roleRepository.findRoleAdmin()
                .orElseThrow(() -> noSuchElementExceptionFactory.getNoSuchElementException(EntityType.ROLE, "name", "ROLE_ADMIN"));
        return result;
    }

    @Override
    @Transactional
    public RoleEntity findRoleManager() {
        RoleEntity result = roleRepository.findRoleManager()
                .orElseThrow(() -> noSuchElementExceptionFactory.getNoSuchElementException(EntityType.ROLE, "name", "ROLE_MANAGER"));
        return result;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!roleRepository.existsById(id)){
            throw noSuchElementExceptionFactory.getNoSuchElementException(EntityType.ROLE, "id", id);
        }
        try{
            roleRepository.deleteById(id);
        } catch (Exception e){
            throw new DataBaseUpdateException("Unable to delete Role with id='" + id + "'");
        }
    }

    @Override
    @Transactional
    public boolean existsById(Long id) {
        return roleRepository.existsById(id);
    }

    @Override
    @Transactional
    public RoleEntity toEntity(RoleDto dto) {
        if (dto == null) throw new InvalidDtoException("DTO is empty");
        if (dto.getId() == null) throw new InvalidDtoException("Invalid Role Id");
        RoleEntity result = roleRepository.findById(dto.getId())
                .orElseThrow(() -> noSuchElementExceptionFactory.getNoSuchElementException(EntityType.ROLE, "id", dto.getId()));
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
