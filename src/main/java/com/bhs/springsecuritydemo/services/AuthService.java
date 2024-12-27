package com.bhs.springsecuritydemo.services;

import com.bhs.springsecuritydemo.dtos.SignInDto;
import com.bhs.springsecuritydemo.dtos.SignUpDto;
import com.bhs.springsecuritydemo.models.User;
import com.bhs.springsecuritydemo.repositories.UserRepository;
import com.bhs.springsecuritydemo.responses.SignInResponse;
import com.bhs.springsecuritydemo.responses.SignUpResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public SignUpResponse createUser(SignUpDto userDetails){
        User user = User
                        .builder()
                        .email(userDetails.getEmail())
                        .name(userDetails.getName())
                        .password(passwordEncoder.encode(userDetails.getPassword()))
                        .createdAt(new Date())
                        .updatedAt(new Date())
                        .build();
        userRepository.save(user);
        return SignUpResponse
                    .builder()
                    .name(user.getName())
                    .email(user.getEmail())
                    .createdAt(user.getCreatedAt())
                    .build();
    }

    public Optional<?> authenticate(SignInDto userDetails){
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userDetails.getEmail(),
                            userDetails.getPassword()
                    )
            );
            User authenticatedUser = userRepository.findUserByEmail(userDetails.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("user not found"));

            String jwtToken = jwtService.generateToken(authenticatedUser);
            return Optional.of(SignInResponse
                    .builder()
                    .email(authenticatedUser.getEmail())
                    .name(authenticatedUser.getName())
                    .token(jwtToken)
                    .expiration(jwtService.getExpirationTime())
                    .build());
        } catch (Exception e) {
            return Optional.of(e.getMessage());
        }
    }

}
