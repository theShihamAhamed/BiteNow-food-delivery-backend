package com.dev.BiteNowAPI.service;

import com.dev.BiteNowAPI.io.UserRequest;
import com.dev.BiteNowAPI.io.UserResponse;

public interface UserService {
    UserResponse registerUser(UserRequest request);

    String findByUserId();
}
