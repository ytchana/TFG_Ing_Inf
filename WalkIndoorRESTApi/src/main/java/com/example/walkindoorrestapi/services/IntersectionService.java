package com.example.walkindoorrestapi.services;


import com.example.walkindoorrestapi.entities.Intersection;
import com.example.walkindoorrestapi.repositories.IntersectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class IntersectionService {

    @Autowired
    private IntersectionRepository repository;

    public List<Intersection> getAllIntersections() {
        return repository.findAll();
    }

    public Intersection saveIntersection(Intersection intersection) {
        return repository.save(intersection);
    }
}

