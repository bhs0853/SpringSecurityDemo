package com.bhs.springsecuritydemo.services;

import com.bhs.springsecuritydemo.models.User;
import com.bhs.springsecuritydemo.repositories.UserRepository;
import com.bhs.springsecuritydemo.responses.SignUpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public List<SignUpResponse> getAllUsers(){
        List<User> userList = userRepository.findAll();
        return userList.stream().map(
                    (user) -> SignUpResponse
                                        .builder()
                                        .name(user.getName())
                                        .email(user.getEmail())
                                        .createdAt(user.getCreatedAt())
                                        .build()
                ).collect(Collectors.toList());
    }

    public SignUpResponse getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();
        User currentUser = (userRepository.findUserByEmail(username)).get();
        return SignUpResponse
                .builder()
                .name(currentUser.getName())
                .email(currentUser.getEmail())
                .createdAt(currentUser.getCreatedAt())
                .build();
    }
}
