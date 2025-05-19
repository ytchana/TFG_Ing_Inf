package com.example.walkindoor.models.entities;

import com.example.walkindoor.api.User;
import com.example.walkindoor.models.markers.Marker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

/**
 * Esta clase gestiona el mapa y sus atributos
 */
@Entity
public class MapFrontend implements Serializable {

    // Declaración de variables
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name; // Nombre del mapa
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner; // Relación con el usuario propietario
    @OneToMany(mappedBy = "map", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Marker> markers; // La lista de todos los marcadores sobre un mapa
    private List<Route> routes; // La lista de todas las rutas (conexiones entre pasos) sobre un mapa

    // Constructor
    public MapFrontend(String name, User owner) {
        this.name = name;
        this.owner = owner;
        this.markers = new ArrayList<>();
        this.routes = new ArrayList<>();
    }

    // Getters y Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Marker> getMarkers() { return markers; }
    public void setMarkers(List<Marker> markers) {
        if (markers != null) {
            this.markers.clear();
            this.markers.addAll(markers);
        } else {
            this.markers = new ArrayList<>();
        }
    }

    public List<Route> getRoutes() { return routes; }
    public void setRoutes(List<Route> routes) {
        if (routes != null) {
            this.routes.clear();
            this.routes.addAll(routes);
        } else {
            this.routes = new ArrayList<>();
        }
    }

    // Métodos adicionales para agregar elementos individualmente
    // Estos métodos serán útiles en el futuro. Se dejan aquí de momento.
    public void addMarker(Marker marker) {
        if (marker != null) {
            markers.add(marker);
        }
    }

    public void addRoute(Route route) {
        if (route != null) {
            routes.add(route);
        }
    }

    public void setUserId(Long userId) {
        this.owner = new User(); // Crear una instancia de usuario si no existe
        this.owner.setId(userId);
    }

    public Long getUserId() {
        return owner != null ? owner.getId() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MapFrontend that = (MapFrontend) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(markers, that.markers) && Objects.equals(routes, that.routes) && Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, markers, routes, owner);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    @Override
    public String toString() {
        return "MapFrontend{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", owner=" + (owner != null ? owner.getUsername() : "null") +
                ", markers=" + markers.size() +
                ", routes=" + routes.size() +
                '}';
    }


}
