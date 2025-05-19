package com.example.walkindoor.models.entities;

import java.util.Objects;

/**
 * Esta clase gestiona las conexiones entre los pasos para crear el recorrido
 */
public class Route {
    // Declaración de variables
    private String path; // Almacena el formato WKT correctamente

    // Constructor
    public Route(String path) {
        if (!path.startsWith("SRID=4326;")) {
            this.path = "SRID=4326;" + path; // Agrega el prefijo solo si falta
        } else {
            this.path = path;
        }
    }

    // Getters y Setters
    public String getPath() { return path; } // Devuelve el valor sin modificarlo
    public void setPath(String path) {
        if (!path.startsWith("SRID=4326;")) {
            this.path = "SRID=4326;" + path;
        } else {
            this.path = path;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return Objects.equals(path, route.path);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(path);
    }
}


