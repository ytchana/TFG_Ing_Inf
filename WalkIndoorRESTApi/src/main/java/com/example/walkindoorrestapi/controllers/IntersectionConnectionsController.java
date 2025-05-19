package com.example.walkindoorrestapi.controllers;

import com.example.walkindoorrestapi.entities.DTO.GeoJsonResponse;
import com.example.walkindoorrestapi.entities.IntersectionConnection;
import com.example.walkindoorrestapi.services.IntersectionConnectionsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/intersection_connections")
public class IntersectionConnectionsController {

    private final IntersectionConnectionsService service;

    public IntersectionConnectionsController(IntersectionConnectionsService service) {
        this.service = service;
    }

    @GetMapping
    public List<IntersectionConnection> getAllConnections() {
        return service.getAllConnections();
    }

    @GetMapping("/shortest-path")
    public List<GeoJsonResponse> getShortestPath(@RequestParam int mapId,
                                                 @RequestParam int startPoint,
                                                 @RequestParam int destinationPoint) {
        return service.getShortestPath(mapId, startPoint, destinationPoint);
    }
}


