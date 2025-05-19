package com.example.walkindoorrestapi.repositories;

import com.example.walkindoorrestapi.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
   // User findByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    //   Optional<User> findByMaps_Id(Long mapId);
}
