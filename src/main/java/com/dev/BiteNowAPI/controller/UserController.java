package com.dev.BiteNowAPI.controller;

import com.dev.BiteNowAPI.io.UserRequest;
import com.dev.BiteNowAPI.io.UserResponse;
import com.dev.BiteNowAPI.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@RequestBody UserRequest request) {
        return userService.registerUser(request);
    }
}
