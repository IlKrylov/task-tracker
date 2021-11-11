package com.krylov.tasktracker.tasktracker_rest_web_service.controller.response.template.Impl;

import com.krylov.tasktracker.tasktracker_rest_web_service.controller.response.template.ResponseTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ResponseTemplateImpl implements ResponseTemplate {

    @Override
    public ResponseEntity getResponseOk(Object obj) {
        return new ResponseEntity(obj, HttpStatus.OK);
    }

    @Override
    public ResponseEntity getResponseBadRequest(String message) {
        return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
    }
}
