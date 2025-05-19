/*
package com.example.walkindoor.models.entities;

import com.example.walkindoor.models.enumerations.FlagType;

import java.util.Objects;

import jakarta.persistence.*;

*/
/**
 * Esta clase gestiona las banderas y sus atributos.
 *//*


@Entity
public class Flag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    public FlagType type; // Tipo de bandera (START, END, WARNING, etc.)
    @ManyToOne
    @JoinColumn(name = "marker_id", nullable = false) // Relación con Marker, siempre debe existir
    private Marker marker;

    // Constructor vacío (necesario para JPA)
    public Flag() {}

    // Constructor con parámetros
    public Flag(FlagType type, Marker marker) {
        this.type = type;
        this.marker = marker;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public FlagType getType() { return type; }
    public void setType(FlagType type) { this.type = type; }
    public Marker getMarker() { return marker; }
    public void setMarker(Marker marker) { this.marker = marker; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Flag flag = (Flag) o;
        return Objects.equals(id, flag.id) && type == flag.type && Objects.equals(marker, flag.marker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, marker);
    }
}


*/
