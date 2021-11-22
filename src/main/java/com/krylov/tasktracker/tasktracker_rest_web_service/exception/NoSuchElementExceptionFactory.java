package com.krylov.tasktracker.tasktracker_rest_web_service.exception;

import com.krylov.tasktracker.tasktracker_rest_web_service.entity.enums.EntityType;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
public class NoSuchElementExceptionFactory {

    public NoSuchElementException getNoSuchElementException(EntityType entityType, String keyParameter, Object value){
        StringBuilder messageTemplate = new StringBuilder();
        messageTemplate
                .append(entityType.toString())
                .append(" with ")
                .append(keyParameter)
                .append("='").append(value.toString()).append("' is not in DataBase");

        NoSuchElementException result = new NoSuchElementException(messageTemplate.toString());
        return result;
    }


}
