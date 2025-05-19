package com.example.walkindoorrestapi.repositories;


import com.example.walkindoorrestapi.entities.Intersection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntersectionRepository extends JpaRepository<Intersection, Long> {
}

