package com.example.walkindoor_tfg.controllers;

import com.example.walkindoor_tfg.models.User;
import com.example.walkindoor_tfg.models.UserDTO;
import com.example.walkindoor_tfg.repositories.UserRepository;
import com.example.walkindoor_tfg.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/add")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.saveUser(user));
    }

    @Autowired
    private UserRepository userRepository;

  /*  @GetMapping("/find")
    public ResponseEntity<User> findUser(@RequestParam String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }*/

    @GetMapping("/find")
    public ResponseEntity<UserDTO> findUser(@RequestParam String username) {
        Optional<User> user = userRepository.findByUsername(username);

        return user.map(u -> ResponseEntity.ok(new UserDTO(u.getId(), u.getUsername(), u.getEmail(), u.getRole())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

}