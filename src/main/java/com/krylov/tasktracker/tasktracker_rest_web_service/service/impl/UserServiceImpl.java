package com.krylov.tasktracker.tasktracker_rest_web_service.service.impl;

import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.UserInfoResponseDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.UserRegistrationRequestDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.UserLinksUpdateRequestDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.enums.ChangeType;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.*;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.enums.EntityStatus;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.enums.EntityType;
import com.krylov.tasktracker.tasktracker_rest_web_service.repository.ProjectRepository;
import com.krylov.tasktracker.tasktracker_rest_web_service.repository.RoleRepository;
import com.krylov.tasktracker.tasktracker_rest_web_service.repository.TaskRepository;
import com.krylov.tasktracker.tasktracker_rest_web_service.repository.UserRepository;
import com.krylov.tasktracker.tasktracker_rest_web_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final RoleRepository roleRepository;
    private final TaskRepository taskRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           ProjectRepository projectRepository,
                           RoleRepository roleRepository,
                           TaskRepository taskRepository,
                           BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.roleRepository = roleRepository;
        this.taskRepository = taskRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    @Transactional
    public Optional<UserEntity> register(UserRegistrationRequestDto userInfo) {

        UserEntity result = new UserEntity();

        result.setUserName(userInfo.getUserName());
        result.setPassword(passwordEncoder.encode(userInfo.getPassword()));

        result.setFirsName(userInfo.getFirstName());
        result.setLastName(userInfo.getLastName());
        result.setEmail(userInfo.getEmail());

        result.setStatus(EntityStatus.ACTIVE);

        List<RoleEntity> roles = new ArrayList<>();
        RoleEntity roleUser = roleRepository.findRoleUser().orElseThrow(() -> new NoSuchElementException("ROLE_USER is not found id DataBase"));
        roles.add(roleUser);
        result.setRoles(roles);

        result.setCreated(new Date());
        result.setUpdated(new Date());


        return Optional.ofNullable(userRepository.save(result));
    }

    @Override
    @Transactional
    public Optional<List<String>> updateLinks(UserLinksUpdateRequestDto updateRequestDto) {
        if (updateRequestDto == null || updateRequestDto.getId() == null) return Optional.empty();
        Long id = updateRequestDto.getId();

        Optional<UserEntity> userEntityOptional = userRepository.findById(id);
        if (userEntityOptional.isEmpty()) return Optional.empty();
        UserEntity userEntity = userEntityOptional.get();

        List<String> result = new ArrayList<>();
        addChanges(EntityType.ROLE, roleRepository, updateRequestDto.getRoleChanges(), userEntity, result);
        addChanges(EntityType.PROJECT, projectRepository, updateRequestDto.getProjectChanges(), userEntity, result);
        addChanges(EntityType.TASK, taskRepository, updateRequestDto.getTaskChanges(), userEntity, result);
        userEntity.setUpdated(new Date());
        return Optional.ofNullable(result);
    }

    @Override
    @Transactional
    public List<UserEntity> getAll() {
        List<UserEntity> result = userRepository.findAll();
        return result;
    }

    @Override
    @Transactional
    public Optional<UserEntity> findById(Long id) {
        Optional<UserEntity> result = userRepository.findById(id);
        return result;
    }

    @Override
    @Transactional
    public Optional<UserEntity> findByName(String userName) {
        Optional<UserEntity> result = Optional.ofNullable(userRepository.findByUserName(userName));
        return result;
    }

    @Override
    @Transactional
    public Optional<List<UserEntity>> findAllProjectUsers(Long projectId) {
        if (!projectRepository.existsById(projectId)) return Optional.empty();
        List<UserEntity> result = userRepository.findAllByProjectId(projectId);
        return Optional.ofNullable(result);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Optional<UserEntity> userEntityOptional = userRepository.findById(id);
        if (userEntityOptional.isEmpty()) return;
        UserEntity userEntity = userEntityOptional.get();

        List<ProjectEntity> projectEntities = userEntity.getProjects();
        projectEntities.stream().forEach(projectEntity -> projectEntity.removeUser(userEntity));

        List<TaskEntity> taskEntities = userEntity.getTasks();
        taskEntities.forEach(taskEntity -> taskEntity.setUser(null));

        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    @Transactional
    public boolean existsByUserName(String userName) {
        return userRepository.existsByUserName(userName);
    }

    @Override
    @Transactional
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public Optional<UserEntity> toEntity(UserInfoResponseDto dto) {
        if (dto == null) return Optional.empty();
        UserEntity result = userRepository.findById(dto.getId()).orElse(new UserEntity());

        result.setUserName(dto.getUserName());
        result.setFirsName(dto.getFirstName());
        result.setLastName(dto.getLastName());

        result.setEmail(dto.getEmail());
        return Optional.of(result);
    }

    @Override
    public UserInfoResponseDto toDto(UserEntity entity) {
        UserInfoResponseDto result = new UserInfoResponseDto();

        result.setId(entity.getId());
        result.setUserName(entity.getUserName());
        result.setFirstName(entity.getFirsName());
        result.setLastName(entity.getLastName());
        result.setEmail(entity.getEmail());

        return result;
    }

    @Override
    public List<UserInfoResponseDto> toDtoList(List<UserEntity> entityList) {
        List<UserInfoResponseDto> result =
                entityList.stream().map(entitity -> toDto(entitity)).collect(Collectors.toList());
        return result;
    }

    private void addChanges(EntityType entityType,
                            JpaRepository repository,
                            Map<Long, ChangeType> changes,
                            UserEntity userEntity,
                            List<String> changeLog) {
        if (changes == null) return;
        for (Map.Entry<Long, ChangeType> changesEntry : changes.entrySet()) {
            Optional<BaseEntity> entityOptional = repository.findById(changesEntry.getKey());
            if (entityOptional.isEmpty()) continue;

            BaseEntity entity = entityOptional.get();
            ChangeType changeType = changesEntry.getValue();

            switch (entityType){
                case ROLE:{
                    if (changeType == ChangeType.ADD) {
                        userEntity.addRole((RoleEntity) entity, changeLog);
                    }
                    if (changeType == ChangeType.REMOVE) {
                        userEntity.removeRole((RoleEntity) entity, changeLog);
                    }
                    break;
                }
                case PROJECT:{
                    if (changeType == ChangeType.ADD) {
                        userEntity.addProject((ProjectEntity) entity, changeLog);
                    }
                    if (changeType == ChangeType.REMOVE) {
                        userEntity.removeProject((ProjectEntity) entity, changeLog);
                    }
                    break;
                }
                case TASK:{
                    if (changeType == ChangeType.ADD) {
                        userEntity.addTask((TaskEntity) entity, changeLog);
                    }
                    if (changeType == ChangeType.REMOVE) {
                        userEntity.removeTask((TaskEntity) entity, changeLog);
                    }
                    break;
                }
            }
        }
    }
}
