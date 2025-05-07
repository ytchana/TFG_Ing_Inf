package com.example.walkindoor_tfg.models;

import com.example.walkindoor_tfg.models.Enum.FlagType;
import jakarta.persistence.*;

import org.locationtech.jts.geom.Point;

import java.util.Objects;

@Entity
@Table(name = "markers")
public class StepMarker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long markerId; // Identificador único del marcador

    @Column(columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point location; // Ubicación geoespacial en formato PostGIS

    @ManyToOne
    @JoinColumn(name = "map_id", nullable = false)
    private CustomMap map; // Relación con la tabla `maps`

    @Column(nullable = true)
    private String note; // Nota opcional asociada al marcador

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private FlagType flagType; // Estado opcional del marcador (ej. "DANGER", "SAFE")

    // Constructor vacío
    public StepMarker() {
    }

    // Constructor con todos los campos
    public StepMarker(Point location, CustomMap map, String note, FlagType flagType) {
        this.location = location;
        this.map = map;
        this.note = note;
        this.flagType = flagType;
    }

    // Getters y Setters
    public Long getMarkerId() {
        return markerId;
    }

    public void setMarkerId(Long markerId) {
        this.markerId = markerId;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public CustomMap getMap() {
        return map;
    }

    public void setMap(CustomMap map) {
        this.map = map;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String notes) {
        this.note = notes;
    }

    public FlagType getFlagType() {
        return flagType;
    }

    public void setFlagType(FlagType flagType) {
        this.flagType = flagType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StepMarker that = (StepMarker) o;
        return Objects.equals(markerId, that.markerId) && Objects.equals(location, that.location) && Objects.equals(map, that.map) && Objects.equals(note, that.note) && flagType == that.flagType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(markerId, location, map, note, flagType);
    }
}
