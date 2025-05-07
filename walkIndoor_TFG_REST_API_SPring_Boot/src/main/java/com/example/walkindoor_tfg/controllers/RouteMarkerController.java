package com.example.walkindoor_tfg.controllers;


import com.example.walkindoor_tfg.models.MarkerDTO;
import com.example.walkindoor_tfg.models.RouteMarker;
import com.example.walkindoor_tfg.services.RouteMarkerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/routes")
public class RouteMarkerController {

    private final RouteMarkerService routeMarkerService;

    public RouteMarkerController(RouteMarkerService routeMarkerService) {
        this.routeMarkerService = routeMarkerService;
    }

    /**
     * Obtener todas las rutas de un mapa.
     *
     * @param mapId ID del mapa.
     * @return Lista de rutas.
     */
    @GetMapping("/map/{mapId}")
    public ResponseEntity<List<RouteMarker>> getRoutesByMap(@PathVariable Long mapId) {
        return ResponseEntity.ok(routeMarkerService.getRoutesByMap(mapId));
    }

    @GetMapping("/{routeId}/markers")
    public ResponseEntity<List<MarkerDTO>> getRouteMarkers(@PathVariable Long routeId) {
        List<MarkerDTO> markers = routeMarkerService.getMarkersByRoute(routeId);
        return ResponseEntity.ok(markers);
    }

    @GetMapping
    public ResponseEntity<List<RouteMarker>> getAllRoutes() {
        return ResponseEntity.ok(routeMarkerService.getAllRoutes());
    }
}


