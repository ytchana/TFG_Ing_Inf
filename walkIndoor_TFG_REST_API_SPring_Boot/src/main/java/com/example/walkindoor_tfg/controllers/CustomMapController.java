package com.example.walkindoor_tfg.controllers;


import com.example.walkindoor_tfg.models.CustomMap;
import com.example.walkindoor_tfg.services.CustomMapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/maps")
public class CustomMapController {

    private final CustomMapService mapService;

    public CustomMapController(CustomMapService mapService) {
        this.mapService = mapService;
    }

    @PostMapping("/add")
    public ResponseEntity<CustomMap> addMap(@RequestBody CustomMap map) {
        return ResponseEntity.ok(mapService.saveMap(map));
    }
}

