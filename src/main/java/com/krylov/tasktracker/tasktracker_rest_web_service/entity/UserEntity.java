package com.krylov.tasktracker.tasktracker_rest_web_service.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    public void addRole(RoleEntity role) {
        if (roles == null) roles = new ArrayList<>();
        roles.add(role);
    }

    public void removeRole(RoleEntity role) {
        if (roles == null) roles = new ArrayList<>();
        roles.remove(role);
    }

    public void addProject(ProjectEntity project) {
        if (projects == null) projects = new ArrayList<>();
        projects.add(project);
    }

    public void removeProject(ProjectEntity project) {
        if (projects == null) projects = new ArrayList<>();
        projects.remove(project);
    }

    public void addTask(TaskEntity task) {
        if (tasks == null) tasks = new ArrayList<>();
        tasks.add(task);
    }

    public void removeTask(TaskEntity task) {
        if (tasks == null) tasks = new ArrayList<>();
        tasks.remove(task);
    }

}
