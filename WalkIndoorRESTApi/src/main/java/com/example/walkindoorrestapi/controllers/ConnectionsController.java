package com.example.walkindoorrestapi.controllers;

import com.example.walkindoorrestapi.entities.Connections;
import com.example.walkindoorrestapi.entities.DTO.ConnectionsRequestDTO;
import com.example.walkindoorrestapi.services.ConnectionsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/connections")
//@RequestMapping("/api/maps/connections")
public class ConnectionsController {

    private final ConnectionsService connectionsService;

    // Constructor
    public ConnectionsController(ConnectionsService connectionsService) {
        this.connectionsService = connectionsService;
    }
    @GetMapping("/searchByMap")
    public ResponseEntity<List<Connections>> searchConnectionsByMap(@RequestParam Long mapId) {
        return ResponseEntity.ok(connectionsService.findConnectionsByMap(mapId));
    }

    @GetMapping("/searchByPoint")
    public ResponseEntity<List<Connections>> searchConnectionsByPoint(@RequestParam Long pointId) {
        return ResponseEntity.ok(connectionsService.findConnectionsByPoint(pointId));
    }

    @PostMapping("/create")
    public ResponseEntity<Connections> createConnection(@RequestBody ConnectionsRequestDTO request) {
        Connections connection = connectionsService.createConnection(
                request.getMapId(),
                request.getStartPointId(),
                request.getEndPointId(),
                request.getDirection(),
                request.getCost()
        );

        return ResponseEntity.ok(connection);
    }



    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteConnections(@PathVariable Long id) {
        connectionsService.deleteConnection(id);
        return ResponseEntity.noContent().build();
    }

 /*   @PostMapping
    public Connections addConnection(@RequestBody Connections connection) {
        return connectionsService.saveConnection(connection);
    }*/

    /*@GetMapping("/{mapId}")
    public List<Connections> getConnections(@PathVariable Long mapId) {
        return connectionsService.getConnectionsByMapId(mapId);
    }*/

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConnection(@PathVariable Long id) {
        if (!connectionsService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        connectionsService.deleteConnection(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/start/{pointId}")
    public ResponseEntity<List<Connections>> getConnectionsStartingFrom(@PathVariable Long pointId) {
        List<Connections> connections = connectionsService.findConnectionsStartingFrom(pointId);
        return ResponseEntity.ok(connections);
    }

    @GetMapping("/end/{pointId}")
    public ResponseEntity<List<Connections>> getConnectionsEndingAt(@PathVariable Long pointId) {
        List<Connections> connections = connectionsService.findConnectionsEndingAt(pointId);
        return ResponseEntity.ok(connections);
    }
}
