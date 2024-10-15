package com.example.bookrent2.common.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AddUserRequest {
    private String email;
    private String password;
    private String passwordCheck;
}
