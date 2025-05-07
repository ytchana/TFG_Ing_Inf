package com.example.walkindoor_tfg.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Esta clase gestiona los atributos del mapa.
 * Un mapa actúa como contenedor de:
 * -> Marcadores y sus Notas y/o banderas.
 * -> Las rutas que conectas los marcadores.
 * 
 */

@Entity
@Table(name = "maps")
public class CustomMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Nombre del mapa

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Relación con el usuario propietario del mapa

    /*@OneToMany(mappedBy = "map", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StepMarker> markers = new ArrayList<>(); // Lista de marcadores asociados*/

    @OneToMany(mappedBy = "map", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("map") // Para evitar referencia circular
    private List<StepMarker> markers = new ArrayList<>();


    @OneToMany(mappedBy = "map", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RouteMarker> routes = new ArrayList<>(); // Lista de rutas en el mapa

    // Constructor vacío
    public CustomMap() {
    }

    // Constructor con parámetros
    public CustomMap(String name, User user) {
        this.name = name;
        this.user = user;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return user;
    }

    public void setOwner(User user) {
        this.user = user;
    }

    public List<StepMarker> getMarkers() {
        return markers;
    }

    public void setMarkers(List<StepMarker> markers) {
        this.markers = markers;
    }

    public List<RouteMarker> getRoutes() {
        return routes;
    }

    public void setRoutes(List<RouteMarker> routes) {
        this.routes = routes;
    }

    // Implementación de equals() y hashCode()
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CustomMap)) return false;
        CustomMap map = (CustomMap) obj;
        return Objects.equals(id, map.id) && Objects.equals(name, map.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}

