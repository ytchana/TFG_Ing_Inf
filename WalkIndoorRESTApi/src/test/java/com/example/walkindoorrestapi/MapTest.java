package com.example.walkindoorrestapi;

import com.example.walkindoorrestapi.entities.Map;
import com.example.walkindoorrestapi.entities.User;
import com.example.walkindoorrestapi.repositories.MapRepository;
import com.example.walkindoorrestapi.repositories.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Este test verifica:
 *          Que la creación de Map con valores correctos funcione (testMapCreation()).
 *          Que la validación de name y description funcione (testMapValidationFailsOnEmptyName() y testMapValidationFailsOnEmptyDescription()).
 */

@SpringBootTest
@Transactional
class MapTest {

    private Validator validator;
    private User owner;
    private Map map;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MapRepository mapRepository;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        owner = userRepository.save(new User("Yanick_2", "yanick@example2.com", "securePassword", null));
        map = mapRepository.save(new Map(1L, owner, "Test Map", "Descripción de prueba", true));
    }

    @Test
    void testMapCreation() {
        assertNotNull(map.getId(), "El ID del mapa no debería ser nulo después de guardarse.");
        assertEquals(owner, map.getOwner());
        assertEquals("Test Map", map.getName());
        assertEquals("Descripción de prueba", map.getDescription());
        assertTrue(map.getIsPublic());
    }

    @Test
    void testMapValidationFailsOnEmptyName() {
        Map invalidMap = new Map(2L, owner, "", "Descripción válida", false);
        Set<ConstraintViolation<Map>> violations = validator.validate(invalidMap);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("El nombre del mapa no puede estar vacío.")));
    }

    @Test
    void testMapValidationFailsOnEmptyDescription() {
        Map invalidMap = new Map(3L, owner, "Nombre válido", "", false);
        Set<ConstraintViolation<Map>> violations = validator.validate(invalidMap);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("La descripción no puede estar vacía.")));
    }

    @Test
    void testUserCanCreateMultipleMaps() {
        Map secondMap = mapRepository.save(new Map(4L, owner, "Otro Mapa", "Otra descripción", false));
        assertNotNull(secondMap.getId());
        assertEquals(owner.getId(), secondMap.getOwner().getId());
        assertEquals("Otro Mapa", secondMap.getName());
    }

}

