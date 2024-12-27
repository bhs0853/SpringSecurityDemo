package com.bhs.springsecuritydemo.controllers;


import com.bhs.springsecuritydemo.responses.SignUpResponse;
import com.bhs.springsecuritydemo.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/v1/users")
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<SignUpResponse>> getAllUsers(){
        List<SignUpResponse> userList = userService.getAllUsers();
        return ResponseEntity.ok(userList);
    }

    @GetMapping("/me")
    public ResponseEntity<SignUpResponse> authenticatedUser(){
        SignUpResponse user = userService.getUser();
        return ResponseEntity.ok(user);
    }

}
