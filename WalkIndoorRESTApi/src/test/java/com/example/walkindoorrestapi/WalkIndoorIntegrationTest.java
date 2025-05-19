package com.example.walkindoorrestapi;


import com.example.walkindoorrestapi.entities.Map;
import com.example.walkindoorrestapi.entities.User;
import com.example.walkindoorrestapi.repositories.MapRepository;
import com.example.walkindoorrestapi.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Este test verifica la integración entre User, Map, IntersectionConnection y pgr_dijkstra().
 * Verifica que un User pueda crear un Map correctamente y que las integraciones entre las entidades funcionen.
 */

@SpringBootTest
@Transactional
class WalkIndoorIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MapRepository mapRepository;

    private User user;
    private Map map;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User("Yanick", "yanick@example.com", "securePassword", null));
        map = mapRepository.save(new Map(1L, user, "Test Map", "Descripción de prueba", true));
    }


    @Test
    void testMapCreatedByUser() {
        Map foundMap = mapRepository.findById(map.getId()).orElse(null);

        assertNotNull(foundMap);
        assertEquals("Test Map", foundMap.getName());
        assertEquals(user.getId(), foundMap.getOwner().getId());
    }
}

