package com.example.walkindoorrestapi.entities;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "points")
public class PointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "map_id")
    private Map map;

    @Column(columnDefinition = "GEOMETRY(Point, 4326)", nullable = false)
    @JsonIgnore
    private Point location; // Usando PostGIS

//    @Transient // No se almacena en BD, pero se recibe en JSON
    private Double latitude;

//    @Transient
    private Double longitude;

    @DecimalMin("0.0")
    @DecimalMax("360.0")
    @Column(nullable = false)
    private Double direction;


    @Column(nullable = false)
    private String note;

    @Column(nullable = false)
    private String flag;

    public PointEntity() {}

    @JsonCreator
    public PointEntity(
            @JsonProperty("map") Map map,
            @JsonProperty("latitude") Double latitude,
            @JsonProperty("longitude") Double longitude,
            @JsonProperty("direction") Double direction,
            @JsonProperty("note") String note,
            @JsonProperty("flag") String flag
    ) {
        this.map = map;
        this.latitude = latitude;
        this.longitude = longitude;
        this.direction = direction;
        this.note = note;
        this.flag = flag;
        generatePoint();
    }

    public PointEntity(Map map, Point location, Double direction, String note, String flag) {
        this.map = map;
        this.location = location;
        this.direction = direction;
        this.note = note;
        this.flag = flag;
    }

    @PrePersist
    @PreUpdate
    public void generatePoint() {
        if (latitude != null && longitude != null) {
            GeometryFactory geometryFactory = new GeometryFactory();
            this.location = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        } else if (this.location == null) {
            throw new IllegalArgumentException("Latitude and longitude are required to create a point.");
        }
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Map getMap() { return map; }
    public void setMap(Map map) { this.map = map; }

    public Point getLocation() { return location; }
    public void setLocation(Point location) { this.location = location; }

    public Double getDirection() { return direction; }
    public void setDirection(Double direction) {
        if (direction < 0 || direction > 360) {
            throw new IllegalArgumentException("Direction must be between 0 and 360 degrees.");
        }
        this.direction = direction;
    }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getFlag() { return flag; }
    public void setFlag(String flag) { this.flag = flag; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PointEntity that = (PointEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(map, that.map) &&
                Objects.equals(location, that.location) && Objects.equals(direction, that.direction) &&
                Objects.equals(note, that.note) && Objects.equals(flag, that.flag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, map, location, direction, note, flag);
    }
}
