package com.krylov.tasktracker.tasktracker_rest_web_service.controller.response.service;

import org.springframework.http.ResponseEntity;

public interface ResponseHandler {

    ResponseEntity getResponseOk(Object obj);

    ResponseEntity getResponseBadRequest(String message);

}
