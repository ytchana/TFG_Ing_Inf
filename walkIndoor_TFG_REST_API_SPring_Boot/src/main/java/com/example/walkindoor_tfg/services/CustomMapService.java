package com.example.walkindoor_tfg.services;

import com.example.walkindoor_tfg.models.CustomMap;
import com.example.walkindoor_tfg.repositories.CustomMapRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomMapService {

    private final CustomMapRepository mapRepository;

    public CustomMapService(CustomMapRepository mapRepository) {
        this.mapRepository = mapRepository;
    }

    public CustomMap saveMap(CustomMap map) {
        return mapRepository.save(map);
    }
}