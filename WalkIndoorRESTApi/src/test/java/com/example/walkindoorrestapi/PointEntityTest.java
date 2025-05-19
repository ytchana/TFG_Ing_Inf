package com.example.walkindoorrestapi;


import com.example.walkindoorrestapi.entities.Map;
import com.example.walkindoorrestapi.entities.PointEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Este test verifica:
 *          Que generatePoint() crea correctamente un Point.
 *          Que setDirection() lanza una excepción si el valor es mayor a 360.
 *          Que los atributos de PointEntity se asignan correctamente.
 */

class PointEntityTest {

    private Map map;
    private PointEntity pointEntity;

    @BeforeEach
    void setUp() {
        map = new Map(); // Suponiendo que Map tiene un constructor vacío
        pointEntity = new PointEntity(map, 40.41678, -3.70379, 90.0, "Test Note", "Test Flag");
    }

    @Test
    void testGeneratePoint() {
        pointEntity.generatePoint();
        assertNotNull(pointEntity.getLocation());
        assertEquals(-3.70379, pointEntity.getLocation().getX());
        assertEquals(40.41678, pointEntity.getLocation().getY());
    }

    @Test
    void testDirectionValidation() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> pointEntity.setDirection(400.0));
        assertEquals("Direction must be between 0 and 360 degrees.", exception.getMessage());

        pointEntity.setDirection(180.0);
        assertEquals(180.0, pointEntity.getDirection());
    }

    @Test
    void testPointEntityAttributes() {
        assertEquals(map, pointEntity.getMap());
        assertEquals("Test Note", pointEntity.getNote());
        assertEquals("Test Flag", pointEntity.getFlag());
        assertEquals(90.0, pointEntity.getDirection());
    }
}

