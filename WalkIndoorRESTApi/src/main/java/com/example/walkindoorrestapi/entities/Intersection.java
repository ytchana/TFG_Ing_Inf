package com.example.walkindoorrestapi.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "intersections")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Intersection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "map_id", nullable = false)
    private Integer mapId;

    @Column(name = "location", columnDefinition = "geometry(Point, 4326)", nullable = false)
    private String location;

    @Column(name = "latitude", nullable = false)
    private Float latitude;

    @Column(name = "longitude", nullable = false)
    private Float longitude;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date createdAt;
}

