package com.krylov.tasktracker.tasktracker_rest_web_service.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
public class ProjectEntity extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "projects")
    private List<UserEntity> users;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "project")
    private List<TaskEntity> tasks;

    public void addUser(UserEntity user) {
        if (users == null) users = new ArrayList<>();
        users.add(user);
    }

    public void removeUser(UserEntity user) {
        if (users == null) users = new ArrayList<>();
        users.remove(user);
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
