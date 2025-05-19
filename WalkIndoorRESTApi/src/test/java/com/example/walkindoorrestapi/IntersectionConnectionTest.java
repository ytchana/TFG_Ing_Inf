package com.example.walkindoorrestapi;


import com.example.walkindoorrestapi.entities.Intersection;
import com.example.walkindoorrestapi.entities.IntersectionConnection;
import com.example.walkindoorrestapi.entities.PointEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Este test verifica:
 *          Que IntersectionConnection se crea correctamente con valores válidos (testIntersectionConnectionCreation()).
 *          Que getGeoJsonPath() devuelve un formato JSON adecuado (testGeoJsonPathFormat()).
 */

class IntersectionConnectionTest {

    private Intersection startPoint;
    private PointEntity endPoint;
    private IntersectionConnection intersectionConnection;

    @BeforeEach
    void setUp() {
        startPoint = new Intersection(1L, 1, "POINT(-3.70379 40.41678)", 40.41678f, -3.70379f, new Date());
        endPoint = new PointEntity(null, 41.3851, 2.1734, 180.0, "End Point", "TEST");
        intersectionConnection = new IntersectionConnection(1L, 1, startPoint, endPoint, "LINESTRING(-3.70379 40.41678, 2.1734 41.3851)", 90.0f, 15.5f, new Date());
    }

    @Test
    void testIntersectionConnectionCreation() {
        assertNotNull(intersectionConnection);
        assertEquals(1L, intersectionConnection.getId());
        assertEquals(1, intersectionConnection.getMapId());
        assertEquals(startPoint, intersectionConnection.getStartPoint());
        assertEquals(endPoint, intersectionConnection.getEndPoint());
        assertEquals("LINESTRING(-3.70379 40.41678, 2.1734 41.3851)", intersectionConnection.getPath());
        assertEquals(90.0f, intersectionConnection.getDirection());
        assertEquals(15.5f, intersectionConnection.getCost());
        assertNotNull(intersectionConnection.getCreatedAt());
    }

    @Test
    void testGeoJsonPathFormat() {
        String geoJson = intersectionConnection.getGeoJsonPath();
        assertTrue(geoJson.startsWith("{\"type\": \"LineString\""));
        assertTrue(geoJson.contains("coordinates"));
    }
}

