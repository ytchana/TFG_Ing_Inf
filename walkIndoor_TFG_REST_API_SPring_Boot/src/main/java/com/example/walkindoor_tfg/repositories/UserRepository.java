package com.example.walkindoor_tfg.repositories;

import com.example.walkindoor_tfg.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username); // Busca el usuario por nombre de usuario
    Optional<User> findByEmail(String email);
}
