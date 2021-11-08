package com.krylov.tasktracker.tasktracker_rest_web_service.controller.response.service.Impl;

import com.krylov.tasktracker.tasktracker_rest_web_service.controller.response.service.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ResponseHandlerImpl implements ResponseHandler {

    @Override
    public ResponseEntity getResponseOk(Object obj) {
        return new ResponseEntity(obj, HttpStatus.OK);
    }

    @Override
    public ResponseEntity getResponseBadRequest(String message) {
        return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
    }
}
