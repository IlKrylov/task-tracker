package com.krylov.tasktracker.tasktracker_rest_web_service.controller.response.template;

import org.springframework.http.ResponseEntity;

public interface ResponseTemplate {

    ResponseEntity getResponseOk(Object obj);

    ResponseEntity getResponseBadRequest(String message);

}
