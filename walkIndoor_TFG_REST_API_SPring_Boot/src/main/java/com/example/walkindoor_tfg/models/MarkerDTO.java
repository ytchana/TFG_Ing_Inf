package com.example.walkindoor_tfg.models;


import com.example.walkindoor_tfg.models.Enum.FlagType;

import java.util.Objects;

/**
 * actúa como un Data Transfer Object (DTO) para enviar solo los datos esenciales de los marcadores sin exponer toda la entidad Marker.
 * permitirá enviar solo la información esencial de los marcadores en las respuestas del backend.
 */
public class MarkerDTO {
    private final Long markerId;
    private final String location; // Puede ser en formato WKT (ej. "POINT(10 10)")
    private final String note;
    private final FlagType flagType;

    public MarkerDTO(Long markerId, String location, String note, FlagType flagType) {
        this.markerId = markerId;
        this.location = location;
        this.note = note;
        this.flagType = flagType;
    }


    // Getters y Setters
    public Long getMarkerId() { return markerId; }
    public String getLocation() { return location; }
    public String getNote() { return note; }
    public FlagType getFlagType() { return flagType; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MarkerDTO markerDTO = (MarkerDTO) o;
        return Objects.equals(markerId, markerDTO.markerId) && Objects.equals(location, markerDTO.location) && Objects.equals(note, markerDTO.note) && Objects.equals(flagType, markerDTO.flagType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(markerId, location, note, flagType);
    }
}
