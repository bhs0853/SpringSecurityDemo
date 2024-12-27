package com.bhs.springsecuritydemo.services;

import com.bhs.springsecuritydemo.dtos.SignInDto;
import com.bhs.springsecuritydemo.dtos.SignUpDto;
import com.bhs.springsecuritydemo.models.User;
import com.bhs.springsecuritydemo.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public User createUser(SignUpDto userDetails){
        User user = User
                        .builder()
                        .email(userDetails.getEmail())
                        .name(userDetails.getName())
                        .password(passwordEncoder.encode(userDetails.getPassword()))
                        .build();
        return userRepository.save(user);
    }

    public User authenticate(SignInDto userDetails){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDetails.getEmail(),
                        userDetails.getPassword()
                )
        );
        return userRepository.findUserByEmail(userDetails.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }

}
