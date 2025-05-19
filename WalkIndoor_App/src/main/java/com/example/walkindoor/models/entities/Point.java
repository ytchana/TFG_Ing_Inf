package com.example.walkindoor.models.entities;


import androidx.annotation.NonNull;

import com.example.walkindoor.models.enumerations.FlagType;

/**
 * Representa un marcador o paso en el mapa.
 * Cada marcador tiene coordenadas (x, y) y una nota opcional asociada.
 * (Esta clase se podía haber creado por separado, pero de momento se deja así porque va muy vinculada con la clase MarkerManager).
 */
public class Point {
    private float x; // Coordenada X en el lienzo del mapa
    private float y; // Coordenada Y en el lienzo del mapa
    private String note; // Nota asociada al punto. De momento se gestiona desde aquí como cadena de texto, y no desde su propia clase. Porque solo tiene un atributo.
    private FlagType flagType; // Tipo de bandera asociada
    private Long markerId; // ID del marcador en la base de datos (opcional)

    /**
     * Constructor para crear un punto con coordenadas, nota y bandera.
     *
     * @param x        Coordenada X del punto.
     * @param y        Coordenada Y del punto.
     * @param note     Nota opcional para describir el punto.
     * @param flagType Tipo de bandera asociada.
     */
    public Point(float x, float y, String note, FlagType flagType) {
        this.x = x;
        this.y = y;
        this.note = note;
        this.flagType = flagType;
    }

    /**
     * Constructor adicional para casos en los que se tiene el ID del marcador en la BD.
     *
     * @param x        Coordenada X.
     * @param y        Coordenada Y.
     * @param note     Nota opcional.
     * @param flagType Tipo de bandera.
     * @param markerId ID del marcador en la BD.
     */
    public Point(float x, float y, String note, FlagType flagType, Long markerId) {
        this.x = x;
        this.y = y;
        this.note = note;
        this.flagType = flagType;
        this.markerId = markerId;
    }

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // Getters y Setters
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }

    public float getY() { return y; }
    public void setY(float y) { this.y = y; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public FlagType getFlagType() { return flagType; }
    public void setFlagType(FlagType flagType) { this.flagType = flagType; }

    public Long getMarkerId() { return markerId; }
    public void setMarkerId(Long markerId) { this.markerId = markerId; }

    /**
     * Convierte el punto en una representación en formato WKT para PostGIS.
     * @return Cadena en formato WKT: "SRID=4326;POINT(x y)"
     */
    public String toWKT() {
        return "SRID=4326;POINT(" + getX() + " " + getY() + ")";
    }

    /**
     * Representación en cadena del punto.
     * @return Información del punto en formato legible.
     */
    @NonNull
    @Override
    public String toString() {
        return "Point{x=" + getX() + ", y=" + getY() + ", note='" + getNote() + "', flagType=" + getFlagType() + ", markerId=" + getMarkerId() + "}";
    }
}
