package com.example.bookrent2.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
public enum  ErrorCode {

    USER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
    ALREADY_REGISTER_USER(HttpStatus.CONFLICT, "유저가 존재합니다."),
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 방이 존재하지 않습니다."),
    USER_ALREADY_EXISTED(HttpStatus.CONFLICT, "해당 유저가 이미 존재합니다."),
    EMAIL_ALREADY_EXISTED(HttpStatus.CONFLICT, "해당 이메일이 이미 존재합니다."),
    JWT_INVALID_TOKEN(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR ON TOKEN"),
    INVALID_CREDENTIALS_SUPPLIED(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid credentials supplied"),
    DEFAULT_ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "Default role not found");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

}
