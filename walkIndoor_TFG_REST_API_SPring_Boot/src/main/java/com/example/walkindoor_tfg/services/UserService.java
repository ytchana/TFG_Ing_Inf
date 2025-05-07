package com.example.walkindoor_tfg.services;

import com.example.walkindoor_tfg.models.User;
import com.example.walkindoor_tfg.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

}
