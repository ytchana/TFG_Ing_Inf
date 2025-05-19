package com.example.walkindoorrestapi.controllers;

import com.example.walkindoorrestapi.entities.User;
import com.example.walkindoorrestapi.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // Constructor
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Crear usuario
    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (user.getUsername() == null || user.getEmail() == null || user.getPassword() == null) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(userService.createUser(user));
    }

    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // Obtener usuario por email
    @GetMapping("/email")
    public ResponseEntity<Optional<User>> getUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    // Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Usuario eliminado correctamente.");
    }

    /*@GetMapping("/searchByMap")
    public ResponseEntity<Optional<User>> searchUserByMap(@RequestParam Long mapId) {
        Optional<User> user = userService.findUserByMapId(mapId);
        return ResponseEntity.ok(user);
    }*/

    @GetMapping("/find")
    public ResponseEntity<User> getUserByIdentifier(@RequestParam String identifier) {
        Optional<User> user = userService.findUserByEmailOrUsername(identifier);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


}
