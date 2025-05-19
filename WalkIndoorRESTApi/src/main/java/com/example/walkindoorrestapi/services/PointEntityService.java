package com.example.walkindoorrestapi.services;

import com.example.walkindoorrestapi.entities.Map;
import com.example.walkindoorrestapi.entities.PointEntity;
import com.example.walkindoorrestapi.repositories.MapRepository;
import com.example.walkindoorrestapi.repositories.PointEntityRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PointEntityService {

    private final PointEntityRepository pointEntityRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory();
    private final MapRepository mapRepository;

    // Constructor
    public PointEntityService(PointEntityRepository pointEntityRepository, MapRepository mapRepository) {
        this.pointEntityRepository = pointEntityRepository;
        this.mapRepository = mapRepository;
    }

    // Crear un punto geoespacial
    public PointEntity createPoint(Long mapId, Double latitude, Double longitude, String note) {
        Map map = mapRepository.findById(mapId)
                .orElseThrow(() -> new IllegalArgumentException("Map not found"));
        PointEntity point = new PointEntity();
        point.setMap(map);
        point.setLatitude(latitude);
        point.setLongitude(longitude);
        point.setLocation(new GeometryFactory().createPoint(new Coordinate(longitude, latitude)));
        point.setNote(note);

        return pointEntityRepository.save(point);
    }


    // Obtener todos los puntos
    public List<PointEntity> getAllPoints() {
        return pointEntityRepository.findAll();
    }

    // Buscar punto por ID
    public Optional<PointEntity> getPointById(Long id) {
        return pointEntityRepository.findById(id);
    }

    // Buscar puntos cercanos
    public List<PointEntity> getNearbyPoints(Double longitude, Double latitude, Double radius) {
        return pointEntityRepository.findNearbyPoints(longitude, latitude, radius);
    }

    // Eliminar un punto por ID
    public void deletePoint(Long id) {
        pointEntityRepository.deleteById(id);
    }

    public List<PointEntity> findPoints(String note, String flag) {
        if (note != null && flag != null) return pointEntityRepository.findByNoteContainingIgnoreCaseAndFlag(note, flag);
        if (note != null) return pointEntityRepository.findByNoteContainingIgnoreCase(note);
        if (flag != null) return pointEntityRepository.findByFlag(flag);
        return pointEntityRepository.findAll();
    }


}
