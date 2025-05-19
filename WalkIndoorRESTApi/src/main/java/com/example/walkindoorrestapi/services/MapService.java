package com.example.walkindoorrestapi.services;

import com.example.walkindoorrestapi.entities.Map;
import com.example.walkindoorrestapi.repositories.MapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MapService {

    @Autowired
    private MapRepository mapRepository;

    // Constructor
    public MapService(MapRepository mapRepository) {
        this.mapRepository = mapRepository;
    }

    // Crear un mapa
    public Map createMap(Map map) {
        return mapRepository.save(map);
    }

    // Obtener todos los mapas
    public List<Map> getAllMaps() {
        return mapRepository.findAll();
    }

    // Buscar mapa por ID
    public Optional<Map> getMapById(Long id) {
        return mapRepository.findById(id);
    }

    // Buscar mapa por notas
    /*public List<Map> findMaps(String note, String flag, Long userId) {
        if (note != null && flag != null && userId != null) {
            return mapRepository.findByNoteContainingIgnoreCaseAndFlagAndUserId(note, flag, userId);
        } else if (note != null && flag != null) {
            return mapRepository.findByNoteContainingIgnoreCaseAndFlag(note, flag);
        } else if (note != null && userId != null) {
            return mapRepository.findByNoteContainingIgnoreCaseAndUserId(note, userId);
        } else if (flag != null && userId != null) {
            return mapRepository.findByFlagAndUserId(flag, userId);
        } else if (note != null) {
            return mapRepository.findByNoteContainingIgnoreCase(note);
        } else if (flag != null) {
            return mapRepository.findByFlag(flag);
        } else if (userId != null) {
            return mapRepository.findByUserId(userId);
        } else {
            return mapRepository.findAll();
        }
    }*/
    public List<Map> findMaps(Long userId) {
        if (userId != null) {
            return mapRepository.findByOwnerId(userId);
        } else {
            return null;
        }
    }

    // Buscar mapas públicos
    public List<Map> getPublicMaps() {
        return mapRepository.findByIsPublicTrue();
    }

    // Eliminar un mapa por ID
    public void deleteMap(Long id) {
        mapRepository.deleteById(id);
    }

    public List<Map> findMapByName(String name) {
        return mapRepository.findByName(name);
    }

}
