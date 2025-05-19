/*
package com.example.walkindoor.models.entities;

import java.util.Objects;

import jakarta.persistence.*;

*/
/**
 * Esta clase gestion la nota y sus atributos.
 *//*


@Entity
public class Note {
    // Variables
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // El id de la nota
    @Column(nullable = false)
    private String content; // Contenido de la nota
    @ManyToOne
    @JoinColumn(name = "marker_id", nullable = false) // Relación con Marker, siempre debe existir
    private Marker marker;

    // Constructor vacío (necesario para JPA)
    public Note() {}

    // Constructor con parámetros
    public Note(String content, Marker marker) {
        this.content = content;
        this.marker = marker;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Marker getMarker() { return marker; }
    public void setMarker(Marker marker) { this.marker = marker; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return Objects.equals(id, note.id) && Objects.equals(content, note.content) && Objects.equals(marker, note.marker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, marker);
    }
}


*/
