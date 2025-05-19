package com.example.walkindoorrestapi.repositories;

import com.example.walkindoorrestapi.entities.IntersectionConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntersectionConnectionsRepository extends JpaRepository<IntersectionConnection, Long> {
    @Query("SELECT ic.id, ic.mapId, ic.startPoint, ic.endPoint, ST_AsGeoJSON(ic.path), ic.direction, ic.cost, ic.createdAt FROM IntersectionConnection ic")
    List<Object[]> getAllConnectionsAsGeoJson();

    @Query(value = """
    SELECT edge FROM pgr_dijkstra(
        'SELECT id, start_point AS source, end_point AS target, cost FROM intersection_connections WHERE map_id = ' || :mapId,
        :startPoint, :destinationPoint, false
    )""", nativeQuery = true)
    List<Integer> findShortestPath(@Param("mapId") int mapId,
                                   @Param("startPoint") int startPoint,
                                   @Param("destinationPoint") int destinationPoint);


    @Query("SELECT ic FROM IntersectionConnection ic WHERE ic.id IN :edges")
    List<IntersectionConnection> findConnectionsByIds(@Param("edges") List<Integer> edges);

    @Query("SELECT ST_AsGeoJSON(ic.path) FROM IntersectionConnection ic WHERE ic.id = :id")
    String getGeoJsonPath(@Param("id") Long id);

}

