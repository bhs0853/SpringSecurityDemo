package com.bhs.springsecuritydemo.controllers;

import com.bhs.springsecuritydemo.dtos.SignInDto;
import com.bhs.springsecuritydemo.dtos.SignUpDto;
import com.bhs.springsecuritydemo.models.User;
import com.bhs.springsecuritydemo.responses.SignInResponse;
import com.bhs.springsecuritydemo.responses.SignUpResponse;
import com.bhs.springsecuritydemo.services.AuthService;
import com.bhs.springsecuritydemo.services.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@RequestBody SignUpDto userDetails){
        SignUpResponse response = authService.createUser(userDetails);
        if(response != null){
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        return ResponseEntity.internalServerError().build();
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody SignInDto userDetails){
        Optional<?> response = authService.authenticate(userDetails);
        if(response.isEmpty()){
            return ResponseEntity.internalServerError().build();
        }
        if(response.get().getClass().equals(SignInResponse.class)){
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response.get());
    }
}
