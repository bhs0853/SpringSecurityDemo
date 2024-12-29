package com.bhs.springsecuritydemo.controllers;

import com.bhs.springsecuritydemo.dtos.SignInDto;
import com.bhs.springsecuritydemo.dtos.SignUpDto;
import com.bhs.springsecuritydemo.responses.SignUpResponse;
import com.bhs.springsecuritydemo.services.AuthService;
import com.bhs.springsecuritydemo.services.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService){
        this.authService = authService;
        this.jwtService = jwtService;
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
    public ResponseEntity<?> authenticateUser(@RequestBody SignInDto userDetails,
                                              HttpServletResponse servletResponse){
        Optional<?> response = authService.authenticate(userDetails);
        if(response.isEmpty()){
            return ResponseEntity.internalServerError().build();
        }
        if(response.get().getClass().equals(String.class)){
            ResponseCookie cookie = ResponseCookie.from("JwtToken", (String) response.get())
                    .httpOnly(true)
                    .maxAge(jwtService.getExpirationTime())
                    .path("/")
                    .secure(false)
                    .build();
            servletResponse.setHeader("Set-Cookie", cookie.toString());
            return ResponseEntity.ok("Message : SignIn Successful");
        }
        Exception e = (Exception) response.get();
        return ResponseEntity.ok(e.getMessage());
    }
}
