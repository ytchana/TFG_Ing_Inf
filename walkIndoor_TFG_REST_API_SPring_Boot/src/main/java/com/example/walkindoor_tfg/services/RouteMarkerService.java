package com.example.walkindoor_tfg.services;

import com.example.walkindoor_tfg.models.MarkerDTO;
import com.example.walkindoor_tfg.models.RouteMarker;
import com.example.walkindoor_tfg.repositories.RouteMarkerRepository;
import org.springframework.stereotype.Service;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.sql.SQLException;

import java.util.List;

/**
 * Gestiona la lógica de negocio relacionada con las rutas (guardar, recuperar y eliminar rutas asociadas a un mapa).
 */

@Service
public class RouteMarkerService {
    private final RouteMarkerRepository routeMarkerRepository;

    public RouteMarkerService(RouteMarkerRepository routeRepository) {
        this.routeMarkerRepository = routeRepository;
    }

    public List<MarkerDTO> getMarkersByRoute(Long routeId) {
        return routeMarkerRepository.findMarkersByRoute(routeId);
    }

    public List<RouteMarker> getRoutesByMap(Long mapId) {
        return routeMarkerRepository.findByMapId(mapId);
    }

    public List<RouteMarker> getAllRoutes() {
        return routeMarkerRepository.findAll();
    }

    public LineString convertToLineString(String wkt) throws SQLException, ParseException {
        Geometry geom = new WKTReader().read(wkt);
        return (LineString) geom;
    }
}
