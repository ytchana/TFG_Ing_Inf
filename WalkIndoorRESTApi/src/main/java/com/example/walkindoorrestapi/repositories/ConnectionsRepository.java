package com.example.walkindoorrestapi.repositories;

import com.example.walkindoorrestapi.entities.Connections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConnectionsRepository extends JpaRepository<Connections, Long> {
    List<Connections> findByMapId(Long mapId);
    List<Connections> findByStartPoint_IdOrEndPoint_Id(Long startPointId, Long endPointId);
    List<Connections> findByStartPoint_Id(Long pointId);
    List<Connections> findByEndPoint_Id(Long pointId);
}
