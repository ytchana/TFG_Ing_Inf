package com.example.walkindoorrestapi.controllers;

import com.example.walkindoorrestapi.entities.PointEntity;
import com.example.walkindoorrestapi.entities.DTO.PointRequestDTO;
import com.example.walkindoorrestapi.services.PointEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/points")
public class PointEntityController {

    @Autowired
    private PointEntityService pointEntityService;

    public PointEntityController(PointEntityService pointEntityService) {
        this.pointEntityService = pointEntityService;
    }

    // Crear un punto
    @PostMapping("/create")
    public ResponseEntity<PointEntity> createPoint(@RequestBody PointRequestDTO request) {
        PointEntity newPoint = pointEntityService.createPoint(request.getMapId(), request.getLatitude(), request.getLongitude(), request.getNote());
        return ResponseEntity.ok(newPoint);
    }


    // Obtener todos los puntos
    @GetMapping
    public ResponseEntity<List<PointEntity>> getAllPoints() {
        return ResponseEntity.ok(pointEntityService.getAllPoints());
    }

    // Obtener punto por ID
    @GetMapping("/{id}")
    public ResponseEntity<PointEntity> getPointById(@PathVariable Long id) {
        return pointEntityService.getPointById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // Obtener puntos cercanos
    @GetMapping("/nearby")
    public ResponseEntity<List<PointEntity>> getNearbyPoints(@RequestParam Double lon, @RequestParam Double lat, @RequestParam Double radius) {
        return ResponseEntity.ok(pointEntityService.getNearbyPoints(lon, lat, radius));
    }

    // Eliminar un punto
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePoint(@PathVariable Long id) {
        pointEntityService.deletePoint(id);
        return ResponseEntity.ok("Punto eliminado correctamente.");
    }

    @GetMapping("/search")
    public ResponseEntity<List<PointEntity>> searchPoints(
            @RequestParam(required = false) String note,
            @RequestParam(required = false) String flag
    ) {
        List<PointEntity> points = pointEntityService.findPoints(note, flag);
        return ResponseEntity.ok(points);
    }
}
