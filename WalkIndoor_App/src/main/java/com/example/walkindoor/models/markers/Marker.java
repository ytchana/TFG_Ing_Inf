package com.example.walkindoor.models.markers;


import com.example.walkindoor.models.entities.MapFrontend;
import com.example.walkindoor.models.enumerations.FlagType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.*;

/**
 * Esta clase gestiona los marcadores y sus atributos.
 * Esta clase es útil para luego enviar los marcadores al Backend en formato geométrico.
 */

@Entity
public class Marker {
    // Variables
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long marker_id;

    @Column(nullable = false)
    private String location; // Ubicación en formato WKT ("POINT(40.7128 -74.0060)")

    @Column(nullable = true)
    private String note; // Nota asociada al marcador

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private FlagType flagType; // Tipo de bandera (START, END, WARNING, etc.)

    @ManyToOne
    @JoinColumn(name = "map_id", nullable = false) // Relación con el mapa
    private MapFrontend map;

    // Constructor vacío
    public Marker() {}

    // Constructor con parámetros
    public Marker(String location, String note, FlagType flagType) {
        this.location = location;
        this.note = note;
        this.flagType = flagType;
    }

    // Getters y Setters
    public Long getId() { return marker_id; }
    public void setId(Long id) { this.marker_id = id; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public FlagType getFlagType() { return flagType; }
    public void setFlagType(FlagType flagType) { this.flagType = flagType; }

    public MapFrontend getMap() { return map; }
    public void setMap(MapFrontend map) { this.map = map; }
}



