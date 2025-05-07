package com.example.walkindoor_tfg.repositories;

import com.example.walkindoor_tfg.models.CustomMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomMapRepository extends JpaRepository<CustomMap, Long> {
    List<CustomMap> findByUserId(Long userId);
}
