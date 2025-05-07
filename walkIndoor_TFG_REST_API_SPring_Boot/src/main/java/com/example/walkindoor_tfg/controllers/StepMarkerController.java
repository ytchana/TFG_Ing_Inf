package com.example.walkindoor_tfg.controllers;

import com.example.walkindoor_tfg.models.MarkerDTO;
import com.example.walkindoor_tfg.models.StepMarker;
import com.example.walkindoor_tfg.services.StepMarkerService;
import org.locationtech.jts.io.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Este controlador recibirá los datos del frontend y los insertará en la base de datos.
 */

@RestController
@RequestMapping("/markers")
public class StepMarkerController {
    private final StepMarkerService stepMarkerService;

  /*  @PostMapping("/add")
    public ResponseEntity<StepMarker> addMarker(@RequestBody StepMarker stepMarker) {
        return ResponseEntity.ok(stepMarkerService.saveMarker(stepMarker));
    }*/

    @Autowired
    public StepMarkerController(StepMarkerService stepMarkerService) {
        this.stepMarkerService = stepMarkerService;
    }

    @PostMapping
    public ResponseEntity<StepMarker> createMarker(@RequestBody MarkerDTO markerDTO) {
        StepMarker savedMarker = stepMarkerService.createMarker(markerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMarker);
    }

    @GetMapping
    public ResponseEntity<List<StepMarker>> getAllMarkers() {
        return ResponseEntity.ok(stepMarkerService.getAllMarkers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StepMarker> getMarkerById(@PathVariable Long id) {
        return stepMarkerService.getMarkerById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<StepMarker> updateMarker(@PathVariable Long id, @RequestBody MarkerDTO markerDTO) {
        StepMarker updatedMarker = stepMarkerService.updateMarker(id, markerDTO);
        return ResponseEntity.ok(updatedMarker);
    }

}
