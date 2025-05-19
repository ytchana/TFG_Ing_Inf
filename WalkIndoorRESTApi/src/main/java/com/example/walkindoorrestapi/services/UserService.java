package com.example.walkindoorrestapi.services;

import com.example.walkindoorrestapi.entities.User;
import com.example.walkindoorrestapi.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    // Constructor
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Crear usuario
    public User createUser(User user) {

        return userRepository.save(user);
    }

    // Obtener todos los usuarios
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Buscar usuario por ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Buscar usuario por email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Eliminar usuario por ID
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> findUserByEmailOrUsername(String identifier) {
        return userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByUsername(identifier)); // Si no lo encuentra por email, busca por username
    }


    /*public Optional<User> findUserByMapId(Long mapId) {
        return userRepository.findByMaps_Id(mapId);
    }*/
}
