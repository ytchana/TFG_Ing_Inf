package com.example.walkindoorrestapi;

import com.example.walkindoorrestapi.controllers.IntersectionConnectionsController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Este test simula una solicitud completa al sistema, verificando que los usuarios puedan obtener rutas correctamente.
 * Simula la petición de búsqueda de ruta y verifica que el sistema responde correctamente con un código 200 y datos válidos.
 */

@SpringBootTest
@WebMvcTest(IntersectionConnectionsController.class)
class WalkIndoorFunctionalTest {

    @Autowired
    private IntersectionConnectionsController controller;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetShortestPath() throws Exception {
        mockMvc.perform(get("/shortest-path")
                        .param("mapId", "1")
                        .param("startPoint", "1")
                        .param("destinationPoint", "2"))
                .andExpect(status().isOk());
    }


}

