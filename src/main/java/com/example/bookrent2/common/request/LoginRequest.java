package com.example.bookrent2.common.request;

import lombok.Data;


@Data
public class LoginRequest {

    private String email;

    private String password;
}
