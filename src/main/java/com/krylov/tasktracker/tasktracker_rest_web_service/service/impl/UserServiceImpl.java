package com.krylov.tasktracker.tasktracker_rest_web_service.service.impl;

import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.UserInfoDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.UserRegistrationRequestDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.UserLinksUpdateRequestDto;
import com.krylov.tasktracker.tasktracker_rest_web_service.dto.user.enums.ChangeType;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.*;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.enums.EntityStatus;
import com.krylov.tasktracker.tasktracker_rest_web_service.entity.enums.EntityType;
import com.krylov.tasktracker.tasktracker_rest_web_service.exception.DataBaseUpdateException;
import com.krylov.tasktracker.tasktracker_rest_web_service.exception.InvalidDtoException;
import com.krylov.tasktracker.tasktracker_rest_web_service.exception.NoSuchElementExceptionFactory;
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
    private final NoSuchElementExceptionFactory noSuchElementExceptionFactory;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           ProjectRepository projectRepository,
                           RoleRepository roleRepository,
                           TaskRepository taskRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           NoSuchElementExceptionFactory noSuchElementExceptionFactory) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.roleRepository = roleRepository;
        this.taskRepository = taskRepository;
        this.passwordEncoder = passwordEncoder;
        this.noSuchElementExceptionFactory = noSuchElementExceptionFactory;
    }


    @Override
    @Transactional
    public UserEntity register(UserRegistrationRequestDto userRegistrationRequestDto) {

        if (userRegistrationRequestDto.getUserName() == null ||
                userRegistrationRequestDto.getPassword() == null ||
                userRegistrationRequestDto.getEmail() == null ||
                userRegistrationRequestDto.getFirstName() == null ||
                userRegistrationRequestDto.getLastName() == null) {
            throw new InvalidDtoException("Invalid DTO data");
        }

        if (userRepository.existsByUserName(userRegistrationRequestDto.getUserName())) {
            throw new DataBaseUpdateException("User with username='" + userRegistrationRequestDto.getUserName() + "' already exists");
        }
        if (userRepository.existsByEmail(userRegistrationRequestDto.getEmail())) {
            throw new DataBaseUpdateException("User with email='" + userRegistrationRequestDto.getEmail() + "' already exists");
        }

        UserEntity userEntity = new UserEntity();

        userEntity.setUserName(userRegistrationRequestDto.getUserName());
        userEntity.setPassword(passwordEncoder.encode(userRegistrationRequestDto.getPassword()));

        userEntity.setFirsName(userRegistrationRequestDto.getFirstName());
        userEntity.setLastName(userRegistrationRequestDto.getLastName());
        userEntity.setEmail(userRegistrationRequestDto.getEmail());

        userEntity.setStatus(EntityStatus.ACTIVE);

        List<RoleEntity> roles = new ArrayList<>();
        RoleEntity roleUser = roleRepository.findRoleUser().orElseThrow(() -> new NoSuchElementException("ROLE_USER is not found id DataBase"));
        roles.add(roleUser);
        userEntity.setRoles(roles);

        userEntity.setCreated(new Date());
        userEntity.setUpdated(new Date());

        try {
            UserEntity result = userRepository.save(userEntity);
            return result;
        } catch (Exception e) {
            throw new DataBaseUpdateException("Unable to save User with username='" + userRegistrationRequestDto.getUserName() + "'");
        }
    }

    @Override
    @Transactional
    public List<String> updateLinks(UserLinksUpdateRequestDto updateRequestDto) {
        if (updateRequestDto == null) throw new InvalidDtoException("DTO is empty");
        if (updateRequestDto.getId() == null) throw new InvalidDtoException("Invalid user Id");

        UserEntity userEntity = userRepository.findById(updateRequestDto.getId())
                .orElseThrow(() -> noSuchElementExceptionFactory.getNoSuchElementException(EntityType.USER, "id", updateRequestDto.getId()));

        List<String> result = new ArrayList<>();
        addChanges(EntityType.ROLE, roleRepository, updateRequestDto.getRoleChanges(), userEntity, result);
        addChanges(EntityType.PROJECT, projectRepository, updateRequestDto.getProjectChanges(), userEntity, result);
        addChanges(EntityType.TASK, taskRepository, updateRequestDto.getTaskChanges(), userEntity, result);
        userEntity.setUpdated(new Date());
        return result;
    }

    @Override
    @Transactional
    public List<UserEntity> getAll() {
        List<UserEntity> result = userRepository.findAll();
        return result;
    }

    @Override
    @Transactional
    public UserEntity findById(Long id) {
        UserEntity result = userRepository.findById(id)
                .orElseThrow(() -> noSuchElementExceptionFactory.getNoSuchElementException(EntityType.USER, "id", id));
        return result;
    }

    @Override
    @Transactional
    public UserEntity findByName(String userName) {
        UserEntity result = userRepository.findByUserName(userName).orElseThrow(
                () -> noSuchElementExceptionFactory.getNoSuchElementException(EntityType.USER, "name", userName));
        return result;
    }

    @Override
    @Transactional
    public List<UserEntity> findAllProjectUsers(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw noSuchElementExceptionFactory.getNoSuchElementException(EntityType.PROJECT, "id", projectId);
        }
        List<UserEntity> result = userRepository.findAllByProjectId(projectId);
        return result;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> noSuchElementExceptionFactory.getNoSuchElementException(EntityType.USER, "id", id));

        if (userEntity.getProjects() != null) {
            List<ProjectEntity> projectEntities = userEntity.getProjects();
            projectEntities.stream().forEach(projectEntity -> projectEntity.removeUser(userEntity));
        }

        if (userEntity.getTasks() != null) {
            List<TaskEntity> taskEntities = userEntity.getTasks();
            taskEntities.forEach(taskEntity -> taskEntity.setUser(null));
        }

        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw new DataBaseUpdateException("Unable to delete User with id='" + id + "'");
        }
    }

    @Override
    @Transactional
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    @Transactional
    public UserEntity toEntity(UserInfoDto dto) {
        if (dto == null) throw new InvalidDtoException("DTO is empty");

        UserEntity result = null;
        if (dto.getId() == null) {
            result = new UserEntity();
            result.setId(0l);
            result.setCreated(new Date());
        } else {
            Optional<UserEntity> userEntityOptional = userRepository.findById(dto.getId());
            result = userEntityOptional.orElseThrow(
                    () -> noSuchElementExceptionFactory.getNoSuchElementException(EntityType.USER, "id", dto.getId()));
        }
        result.setUserName(dto.getUserName());
        result.setFirsName(dto.getFirstName());
        result.setLastName(dto.getLastName());
        result.setEmail(dto.getEmail());

        return result;
    }

    @Override
    public UserInfoDto toDto(UserEntity entity) {
        UserInfoDto result = new UserInfoDto();

        result.setId(entity.getId());
        result.setUserName(entity.getUserName());
        result.setFirstName(entity.getFirsName());
        result.setLastName(entity.getLastName());
        result.setEmail(entity.getEmail());

        return result;
    }

    @Override
    public List<UserInfoDto> toDtoList(List<UserEntity> entityList) {
        List<UserInfoDto> result =
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

            switch (entityType) {
                case ROLE: {
                    if (changeType == ChangeType.ADD) {
                        userEntity.addRole((RoleEntity) entity, changeLog);
                    }
                    if (changeType == ChangeType.REMOVE) {
                        userEntity.removeRole((RoleEntity) entity, changeLog);
                    }
                    break;
                }
                case PROJECT: {
                    if (changeType == ChangeType.ADD) {
                        userEntity.addProject((ProjectEntity) entity, changeLog);
                    }
                    if (changeType == ChangeType.REMOVE) {
                        userEntity.removeProject((ProjectEntity) entity, changeLog);
                    }
                    break;
                }
                case TASK: {
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
