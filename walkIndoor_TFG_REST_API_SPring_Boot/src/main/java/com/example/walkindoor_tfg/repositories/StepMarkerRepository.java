package com.example.walkindoor_tfg.repositories;

import com.example.walkindoor_tfg.models.StepMarker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StepMarkerRepository extends JpaRepository<StepMarker, Long> {

  /*  // Obtener todos los marcadores asociados a un mapa
    @Query("SELECT sm FROM StepMarker sm WHERE sm.map.id = :mapId")
    List<StepMarker> findByMapId(@Param("mapId") Long mapId);*/

    List<StepMarker> findByMapId(Long mapId);

    // Buscar marcadores por nota específica
    List<StepMarker> findByNoteContaining(String keyword);

    // Buscar marcadores por tipo de bandera
    List<StepMarker> findByFlagType(String flag);
}
