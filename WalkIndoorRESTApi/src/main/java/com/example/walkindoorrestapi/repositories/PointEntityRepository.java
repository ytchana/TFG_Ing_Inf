package com.example.walkindoorrestapi.repositories;

import com.example.walkindoorrestapi.entities.PointEntity;
import org.springframework.lang.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PointEntityRepository extends JpaRepository<PointEntity, Long> {

    @Query(value = "SELECT * FROM points WHERE ST_DWithin(location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326), :radius)", nativeQuery = true)
    List<PointEntity> findNearbyPoints(@Param("longitude") Double longitude, @Param("latitude") Double latitude, @Param("radius") Double radius);

    List<PointEntity> findByNoteContainingIgnoreCase(String note);
    List<PointEntity> findByFlag(String flag);
    List<PointEntity> findByNoteContainingIgnoreCaseAndFlag(String note, String flag);
    Optional<PointEntity> findById(Long id);
}

