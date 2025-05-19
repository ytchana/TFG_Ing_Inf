package com.example.walkindoorrestapi.services;

import com.example.walkindoorrestapi.entities.DTO.GeoJsonResponse;
import com.example.walkindoorrestapi.entities.IntersectionConnection;
import com.example.walkindoorrestapi.repositories.IntersectionConnectionsRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class IntersectionConnectionsService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private IntersectionConnectionsRepository repository;

    /**
     * Obtiene todas las conexiones de intersección.
     */
    public List<IntersectionConnection> getAllConnections() {
        return repository.findAll();
    }

    /**
     * Obtiene el camino más corto entre dos puntos utilizando `pgr_dijkstra()`.
     */
    public List<GeoJsonResponse> getShortestPath(int mapId, int startPoint, int destinationPoint) {
        List<Integer> edges = repository.findShortestPath(mapId, startPoint, destinationPoint);
        List<IntersectionConnection> connections = repository.findConnectionsByIds(edges);

        return connections.stream().map(connection -> new GeoJsonResponse(
                connection.getId(),
                connection.getMapId(),
                connection.getStartPoint(),
                connection.getEndPoint(),
                repository.getGeoJsonPath(connection.getId()),  // 🔹 Obtener GeoJSON desde PostgreSQL
                connection.getDirection(),
                connection.getCost(),
                connection.getCreatedAt()
        )).toList();
    }


    /**
     * Convierte el campo `path` a formato GeoJSON.
     */
    public String getGeoJsonPath(Long id) {
        return entityManager.createQuery(
                        "SELECT ST_AsGeoJSON(ic.path) FROM IntersectionConnection ic WHERE ic.id = :id", String.class)
                .setParameter("id", id)
                .getSingleResult();
    }
}
