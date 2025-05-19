package com.example.walkindoorrestapi.repositories;

import com.example.walkindoorrestapi.entities.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MapRepository extends JpaRepository<Map, Long> {
    List<Map> findByIsPublicTrue();
    List<Map> findByOwnerId(Long ownerId);

    // Verificar si un mapa existe por su ID
    boolean existsById(Long id);

    // Buscar un mapa por su nombre
    List<Map> findByName(String name);

}
