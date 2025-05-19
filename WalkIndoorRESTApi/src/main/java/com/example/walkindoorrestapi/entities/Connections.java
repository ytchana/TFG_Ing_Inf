package com.example.walkindoorrestapi.entities;


import com.example.walkindoorrestapi.entities.DTO.GeometrySerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;

@Entity
@Table(name = "connections")
public class Connections {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "map_id", nullable = false)
    private Long mapId;

    @ManyToOne
    @JoinColumn(name = "start_point", nullable = false)
    private PointEntity startPoint;

    @ManyToOne
    @JoinColumn(name = "end_point", nullable = false)
    private PointEntity endPoint;

    @JsonSerialize(using = GeometrySerializer.class)
//    @JsonDeserialize(using = GeometryDeserializer.class)
    @Column(name = "path", columnDefinition = "geometry(LineString,4326)", nullable = false)
    private LineString path; // Almacena la línea geoespacial entre dos puntos

    @Column(name = "direction")
    private Float direction;

    @Column(name = "cost", nullable = false)
    private Float cost;

    public Connections() {}

    @JsonCreator
    public Connections(
            @JsonProperty("mapId") Long mapId,
            @JsonProperty("startPoint") PointEntity startPoint,
            @JsonProperty("endPoint") PointEntity endPoint,
            @JsonProperty("direction") Float direction,
            @JsonProperty("cost") Float cost
    ) {
        this.mapId = mapId;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.direction = direction;
        this.cost = cost;
        generatePath();
    }

    @PrePersist
    @PreUpdate
    public void generatePath() {
        if (startPoint != null && endPoint != null) {
            GeometryFactory geometryFactory = new GeometryFactory();
            Coordinate[] coordinates = {
                    new Coordinate(startPoint.getLongitude(), startPoint.getLatitude()),
                    new Coordinate(endPoint.getLongitude(), endPoint.getLatitude())
            };
            this.path = geometryFactory.createLineString(coordinates);
        }else {
            throw new IllegalArgumentException("startPoint and endPoint are required");
        }
    }

    // Getters y setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMapId() {
        return mapId;
    }

    public void setMapId(Long mapId) {
        this.mapId = mapId;
    }

    public PointEntity getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(PointEntity startPoint) {
        this.startPoint = startPoint;
    }

    public PointEntity getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(PointEntity endPoint) {
        this.endPoint = endPoint;
    }

    public LineString getPath() {
        return path;
    }

    public void setPath(LineString path) {
        this.path = path;
    }

    public Float getDirection() {
        return direction;
    }

    public void setDirection(Float direction) {
        this.direction = direction;
    }

    public Float getCost() {
        return cost;
    }

    public void setCost(Float cost) {
        this.cost = cost;
    }
}

