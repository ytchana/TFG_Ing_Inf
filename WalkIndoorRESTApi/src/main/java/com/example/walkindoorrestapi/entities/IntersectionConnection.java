package com.example.walkindoorrestapi.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "intersection_connections")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class IntersectionConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "map_id", nullable = false)
    private Integer mapId;

    @ManyToOne
    @JoinColumn(name = "start_point", nullable = false)
    private Intersection startPoint;

    @ManyToOne
    @JoinColumn(name = "end_point", nullable = false)
    private PointEntity endPoint;

    @Column(name = "path", columnDefinition = "geometry(LineString, 4326)", nullable = false)
    private String path;

    @Column(name = "direction", nullable = false)
    private Float direction;

    @Column(name = "cost", nullable = false)
    private Float cost;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date createdAt;

    public String getGeoJsonPath() {
        return "{" + "\"type\": \"LineString\", \"coordinates\": " + path + "}";
    }

}

