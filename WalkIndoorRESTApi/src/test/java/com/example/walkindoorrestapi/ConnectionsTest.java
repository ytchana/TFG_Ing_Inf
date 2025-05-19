package com.example.walkindoorrestapi;


import com.example.walkindoorrestapi.entities.Connections;
import com.example.walkindoorrestapi.entities.PointEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionsTest {

    private PointEntity startPoint;
    private PointEntity endPoint;
    private Connections connection;

    @BeforeEach
    void setUp() {
        startPoint = new PointEntity(null, 40.41678, -3.70379, 90.0, "Start Point", "TEST");
        endPoint = new PointEntity(null, 41.3851, 2.1734, 270.0, "End Point", "TEST");
        connection = new Connections(1L, startPoint, endPoint, 180.0f, 10.5f);
    }

    @Test
    void testGeneratePath() {
        connection.generatePath();
        assertNotNull(connection.getPath());
        assertEquals(2, connection.getPath().getNumPoints());

        Coordinate[] coordinates = connection.getPath().getCoordinates();
        assertEquals(-3.70379, coordinates[0].getX());
        assertEquals(40.41678, coordinates[0].getY());
        assertEquals(2.1734, coordinates[1].getX());
        assertEquals(41.3851, coordinates[1].getY());
    }

    @Test
    void testInvalidPathGeneration() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Connections invalidConnection = new Connections(1L, null, endPoint, 180.0f, 10.5f);
            invalidConnection.generatePath();
        });
        assertEquals("startPoint and endPoint are required", exception.getMessage());
    }

    @Test
    void testConnectionsAttributes() {
        assertEquals(1L, connection.getMapId());
        assertEquals(startPoint, connection.getStartPoint());
        assertEquals(endPoint, connection.getEndPoint());
        assertEquals(180.0f, connection.getDirection());
        assertEquals(10.5f, connection.getCost());
    }
}

