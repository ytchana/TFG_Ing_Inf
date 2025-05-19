package com.example.walkindoorrestapi.controllers;


import com.example.walkindoorrestapi.entities.Intersection;
import com.example.walkindoorrestapi.services.IntersectionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/intersections")
public class IntersectionController {

    private final IntersectionService service;

    public IntersectionController(IntersectionService service) {
        this.service = service;
    }

    @GetMapping
    public List<Intersection> getAllIntersections() {
        return service.getAllIntersections();
    }

    @PostMapping
    public Intersection saveIntersection(@RequestBody Intersection intersection) {
        return service.saveIntersection(intersection);
    }
}

