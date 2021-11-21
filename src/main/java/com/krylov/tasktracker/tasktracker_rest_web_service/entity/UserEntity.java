package com.krylov.tasktracker.tasktracker_rest_web_service.entity;

import com.krylov.tasktracker.tasktracker_rest_web_service.entity.enums.EntityType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserEntity extends BaseEntity {

    @Column(name = "user_name")
    private String userName;

    @Column(name = "first_name")
    private String firsName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private List<RoleEntity> roles;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "users_projects",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "project_id", referencedColumnName = "id")})
    private List<ProjectEntity> projects;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<TaskEntity> tasks;

    public void addRole(RoleEntity role, List<String> changeLog) {
        if (role == null) return;
        if (roles == null) {
            roles = new ArrayList<>();
        } else {
            if (roles.contains(role)) {
                addNoteToChangeLog(changeLog, EntityType.ROLE, role.getId(), role.getName().toString(),
                        "ADD REQUEST DENIED, CAUSE: ALREADY ASSIGNED");
                return;
            }
        }
        roles.add(role);
        addNoteToChangeLog(changeLog, EntityType.ROLE, role.getId(), role.getName().toString(),
                "ADDED SUCCESSFULLY");
    }

    public void removeRole(RoleEntity role, List<String> changeLog) {
        if (role == null || roles == null || !roles.contains(role)) return;
        roles.remove(role);
        addNoteToChangeLog(changeLog, EntityType.ROLE, role.getId(), role.getName().toString(),
                "REMOVED SUCCESSFULLY");

    }

    public void addProject(ProjectEntity project, List<String> changeLog) {
        if (project == null) return;
        if (projects == null) {
            projects = new ArrayList<>();
        } else {
            if (projects.contains(project)) {
                addNoteToChangeLog(changeLog, EntityType.PROJECT, project.getId(), project.getName(),
                        "ADD REQUEST DENIED, CAUSE: ALREADY ASSIGNED");
                return;
            }
        }
        projects.add(project);
        addNoteToChangeLog(changeLog, EntityType.PROJECT, project.getId(), project.getName(),
                "ADDED SUCCESSFULLY");

    }

    public void removeProject(ProjectEntity project, List<String> changeLog) {
        if (project == null || projects == null || !projects.contains(project)) return;
        if (tasks != null) {
            List<TaskEntity> tasksToRemove = tasks.stream().filter(taskEntity -> taskEntity.getProject().equals(project)).collect(Collectors.toList());
            tasksToRemove.forEach(taskEntity -> removeTask(taskEntity, changeLog));
        }
        projects.remove(project);
        addNoteToChangeLog(changeLog, EntityType.PROJECT, project.getId(), project.getName(),
                "REMOVED SUCCESSFULLY");
    }

    public void addTask(TaskEntity task, List<String> changeLog) {
        if (task == null) return;
        if (tasks == null) {
            tasks = new ArrayList<>();
        } else {
            if (tasks.contains(task)) {
                addNoteToChangeLog(changeLog, EntityType.TASK, task.getId(), task.getName(),
                        "ADD REQUEST DENIED, CAUSE: ALREADY ASSIGNED");
                return;
            }
        }

        if (projects.contains(task.getProject())) {
            task.setUser(this);
            tasks.add(task);
            addNoteToChangeLog(changeLog, EntityType.TASK, task.getId(), task.getName(),
                    "ADDED SUCCESSFULLY");
        } else{
            addNoteToChangeLog(changeLog, EntityType.TASK, task.getId(), task.getName(), "" +
                    "ADD REQUEST DENIED, CAUSE: USER IS NOT ASSIGNED ON TASK'S PROJECT");
        }
    }

    public void removeTask(TaskEntity task, List<String> changeLog) {
        if (task == null || tasks == null || !tasks.contains(task)) return;
        task.setUser(null);
        tasks.remove(task);
        addNoteToChangeLog(changeLog, EntityType.TASK, task.getId(), task.getName(),
                "REMOVED SUCCESSFULLY");

    }

    private void addNoteToChangeLog(List<String> changeLog, EntityType entityType, Long entityId, String entityName, String text) {
        StringBuilder noteTemplate = new StringBuilder();
        noteTemplate
                .append(entityType.toString())
                .append(" (id = '").append(entityId).append("', name = '").append(entityName).append("') : ")
                .append(text);
        changeLog.add(noteTemplate.toString());
    }
}
