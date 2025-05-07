package com.example.walkindoor_tfg.services;

import com.example.walkindoor_tfg.models.MarkerDTO;
import com.example.walkindoor_tfg.models.StepMarker;
import com.example.walkindoor_tfg.repositories.StepMarkerRepository;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Gestiona la lógica de negocio relacionada con los marcadores.
 */
@Service
public class StepMarkerService {
    private final StepMarkerRepository stepMarkerRepository;

    @Autowired
    public StepMarkerService(StepMarkerRepository stepMarkerRepository) {
        this.stepMarkerRepository = stepMarkerRepository;
    }

    // **Crear un nuevo marcador**
    public StepMarker createMarker(MarkerDTO markerDTO) {
        StepMarker marker = convertDTOToEntity(markerDTO);
        marker.setLocation(convertWKTToPoint(markerDTO.getLocation())); // Convertir WKT a Point
        marker.setNote(markerDTO.getNote());
        marker.setFlagType(markerDTO.getFlagType());

        return stepMarkerRepository.save(marker);
    }

    // **Obtener todos los marcadores**
    public List<StepMarker> getAllMarkers() {
        return stepMarkerRepository.findAll();
    }

    // **Obtener un marcador por ID**
    public Optional<StepMarker> getMarkerById(Long id) {
        return stepMarkerRepository.findById(id);
    }

    // **Actualizar un marcador existente**
    public StepMarker updateMarker(Long id, MarkerDTO markerDTO) {
        return stepMarkerRepository.findById(id)
                .map(marker -> {
                    marker.setLocation(convertWKTToPoint(markerDTO.getLocation()));
                    marker.setNote(markerDTO.getNote());
                    marker.setFlagType(markerDTO.getFlagType());
                    return stepMarkerRepository.save(marker);
                }).orElseThrow(() -> new RuntimeException("Marcador no encontrado"));
    }

    // **Eliminar un marcador por ID**
    public void deleteMarker(Long id) {
        stepMarkerRepository.deleteById(id);
    }

    // **Conversión de WKT a Point (PostGIS)**
    private Point convertWKTToPoint(String wkt) {
        try {
            WKTReader reader = new WKTReader();
            Geometry geometry = reader.read(wkt);

            if (geometry instanceof Point) {
                geometry.setSRID(4326); // Establecer el SRID correcto
                return (Point) geometry;
            } else {
                throw new IllegalArgumentException("La ubicación no es un punto válido");
            }
        } catch (ParseException e) {
            throw new RuntimeException("Error al convertir WKT a Geometry", e);
        }
    }

    public StepMarker convertDTOToEntity(MarkerDTO markerDTO) {
        StepMarker marker = new StepMarker();
        marker.setLocation(convertWKTToPoint(markerDTO.getLocation())); // Convierte la ubicación a `Point`
        marker.setNote(markerDTO.getNote());
        marker.setFlagType(markerDTO.getFlagType());
        return marker;
    }


}

