package com.example.walkindoorrestapi.services;

import com.example.walkindoorrestapi.entities.Connections;
import com.example.walkindoorrestapi.entities.PointEntity;
import com.example.walkindoorrestapi.repositories.ConnectionsRepository;
import com.example.walkindoorrestapi.repositories.PointEntityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionsService {
    private final ConnectionsRepository connectionsRepository;
    private final PointEntityRepository pointEntityRepository;

    public ConnectionsService(ConnectionsRepository connectionsRepository, PointEntityRepository pointEntityRepository) {
        this.connectionsRepository = connectionsRepository;
        this.pointEntityRepository = pointEntityRepository;
    }

    public List<Connections> findConnectionsByMap(Long mapId) {
        return connectionsRepository.findByMapId(mapId);
    }

    public List<Connections> findConnectionsByPoint(Long pointId) {
        return connectionsRepository.findByStartPoint_IdOrEndPoint_Id(pointId, pointId);
    }

    public List<Connections> findConnectionsStartingFrom(Long pointId) {
        return connectionsRepository.findByStartPoint_Id(pointId);
    }

    public List<Connections> findConnectionsEndingAt(Long pointId) {
        return connectionsRepository.findByEndPoint_Id(pointId);
    }

    public Connections createConnection(Long mapId, Long startPointId, Long endPointId, Float direction, Float cost) {
        PointEntity start = pointEntityRepository.findById(startPointId)
                .orElseThrow(() -> new IllegalArgumentException("Start point not found"));

        PointEntity end = pointEntityRepository.findById(endPointId)
                .orElseThrow(() -> new IllegalArgumentException("End point not found"));

        Connections connection = new Connections(mapId, start, end, direction, cost);
        connection.generatePath();

        return connectionsRepository.save(connection);
    }


    public Connections saveConnection(Connections connection) {
        return connectionsRepository.save(connection);
    }

    public void deleteConnection(Long id) {
        connectionsRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return connectionsRepository.existsById(id);
    }

}

