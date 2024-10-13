package com.example.bookrent2.model.user;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean isOAuth2;

    @Builder
    public User(String username, String password,
                String email, Role role, boolean isOAuth2) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.isOAuth2 = isOAuth2;
    }
}
