package com.krylov.tasktracker.tasktracker_rest_web_service.service;

import java.util.List;
import java.util.Optional;

public interface BaseEntityService<E, D> {

    List<E> getAll();

    E findById(Long id);

    E findByName(String name);

    void deleteById(Long id);

    boolean existsById(Long id);

    D toDto(E entity);

    List<D> toDtoList(List<E> entityList);

    E toEntity(D dto);

}
