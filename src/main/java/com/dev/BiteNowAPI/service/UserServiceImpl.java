package com.dev.BiteNowAPI.service;

import com.dev.BiteNowAPI.entity.UserEntity;
import com.dev.BiteNowAPI.io.UserRequest;
import com.dev.BiteNowAPI.io.UserResponse;
import com.dev.BiteNowAPI.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse registerUser(UserRequest request) {
        UserEntity newUser = convertToEntity(request);
        newUser = userRepository.save(newUser);
        return convertToResponse(newUser);

    }

    private UserEntity convertToEntity(UserRequest request) {
        return UserEntity.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .name(request.getName())
                .build();
    }

    private UserResponse convertToResponse(UserEntity registeredUser) {
        return UserResponse.builder()
                .id(registeredUser.getId())
                .email(registeredUser.getEmail())
                .name(registeredUser.getName())
                .build();
    }
}
