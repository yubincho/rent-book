package com.example.bookrent2.rentBook.service;

import com.example.bookrent2.common.exception.ErrorCode;
import com.example.bookrent2.common.exception.SimpleApplicationException;
import com.example.bookrent2.rentBook.model.user.User;
import com.example.bookrent2.rentBook.model.user.UserRepository;
import com.example.bookrent2.common.request.AddUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new SimpleApplicationException(ErrorCode.USER_NOT_FOUND_EXCEPTION, "User not found!"));
    }

    public void save(AddUserRequest dto) {
        userRepository.save(User.builder()
                .email(dto.getEmail())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .build());
    }

    public User oauthSave(User entity) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return userRepository.save(User.builder()
                .email(entity.getEmail())
                .password(encoder.encode(entity.getPassword()))
                .build());
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


}
