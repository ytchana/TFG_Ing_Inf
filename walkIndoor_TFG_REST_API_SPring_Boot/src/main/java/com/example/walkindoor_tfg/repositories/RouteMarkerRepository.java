package com.example.walkindoor_tfg.repositories;

import com.example.walkindoor_tfg.models.CustomMap;
import com.example.walkindoor_tfg.models.MarkerDTO;
import com.example.walkindoor_tfg.models.RouteMarker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteMarkerRepository extends JpaRepository<RouteMarker, Long> {

    /**
     * Encuentra marcadores asociados a una ruta específica y los devuelve como DTOs.
     */
    @Query("SELECT new com.example.walkindoor_tfg.models.MarkerDTO(m.markerId, CAST(ST_AsText(m.location) AS string), m.note, m.flagType) " +
            "FROM MarkerConnections rm " +
            "JOIN rm.marker m " +
            "WHERE rm.route.routeId = :routeId")
    List<MarkerDTO> findMarkersByRoute(@Param("routeId") Long routeId);



    /**
     * Encuentra todas las rutas asociadas a un mapa específico.
     */
    List<RouteMarker> findByMap(CustomMap map);

    /**
     * Encuentra todas las rutas asociadas a un mapa mediante su ID.
     */
    List<RouteMarker> findByMapId(Long mapId);

    /**
     * Encuentra una ruta por su ID y carga sus marcadores asociados.
     */
    @Query("SELECT r FROM RouteMarker r LEFT JOIN FETCH r.markerConnections WHERE r.routeId = :routeId")
    RouteMarker findRouteWithMarkers(@Param("routeId") Long routeId);

    /**
     * Busca todas las rutas que contienen un marcador específico.
     */
    @Query("SELECT r FROM RouteMarker r JOIN r.markerConnections rm WHERE rm.marker.markerId = :markerId")
    List<RouteMarker> findRoutesByMarker(@Param("markerId") Long markerId);

}
