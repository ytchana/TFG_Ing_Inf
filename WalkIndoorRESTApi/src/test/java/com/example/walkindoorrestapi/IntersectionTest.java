package com.example.walkindoorrestapi;

import com.example.walkindoorrestapi.entities.Intersection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Este test verifica:
 *          Que la creación de Intersection con valores correctos funcione (testIntersectionCreation()).
 *          Que la location tiene el formato correcto (testLocationFormat()).
 *          Que los valores de latitude, longitude y location pueden actualizarse correctamente (testUpdateCoordinates()).
 */

class IntersectionTest {

    private Intersection intersection;

    @BeforeEach
    void setUp() {
        intersection = new Intersection(
                1L, 1, "POINT(-3.70379 40.41678)", 40.41678f, -3.70379f, new Date()
        );
    }

    @Test
    void testIntersectionCreation() {
        assertNotNull(intersection);
        assertEquals(1L, intersection.getId());
        assertEquals(1, intersection.getMapId());
        assertEquals("POINT(-3.70379 40.41678)", intersection.getLocation());
        assertEquals(40.41678f, intersection.getLatitude());
        assertEquals(-3.70379f, intersection.getLongitude());
        assertNotNull(intersection.getCreatedAt());
    }

    @Test
    void testLocationFormat() {
        assertTrue(intersection.getLocation().startsWith("POINT("));
        assertTrue(intersection.getLocation().contains(" "));
    }

    @Test
    void testUpdateCoordinates() {
        intersection.setLatitude(41.3851f);
        intersection.setLongitude(2.1734f);
        intersection.setLocation("POINT(2.1734 41.3851)");

        assertEquals(41.3851f, intersection.getLatitude());
        assertEquals(2.1734f, intersection.getLongitude());
        assertEquals("POINT(2.1734 41.3851)", intersection.getLocation());
    }
}

