package com.example.walkindoor_tfg.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.locationtech.jts.geom.LineString;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "routes")
public class RouteMarker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routeId;

    @Column(columnDefinition = "geometry(LineString, 4326)", nullable = false)
    private LineString path; // Define la ruta como una línea en el mapa

    @ManyToOne
    @JoinColumn(name = "map_id", nullable = false)
    @JsonIgnore
    private CustomMap map; // Relación con el mapa al que pertenece la ruta

    /*@ManyToMany
    @JoinTable(
            name = "route_markers",
            joinColumns = @JoinColumn(name = "route_id"),
            inverseJoinColumns = @JoinColumn(name = "marker_id")
    )
    private List<MarkerConnections> markerConnections = new ArrayList<>(); // Lista de marcadores conectados por esta ruta*/

    @OneToMany(mappedBy = "route")
    private List<MarkerConnections> markerConnections;


    // Constructor vacío
    public RouteMarker() {
    }

    // Constructor con parámetros
    public RouteMarker(LineString path, CustomMap map) {
        this.path = path;
        this.map = map;
    }

    // Getters y setters
    public Long getId() {
        return routeId;
    }

    public void setId(Long id) {
        this.routeId = id;
    }

    public LineString getPath() {
        return path;
    }

    public void setPath(LineString path) {
        this.path = path;
    }

    public CustomMap getMap() {
        return map;
    }

    public void setMap(CustomMap map) {
        this.map = map;
    }

    public List<MarkerConnections> getMarkers() {
        return markerConnections;
    }

    public void setMarkers(List<MarkerConnections> markers) {
        this.markerConnections = markers;
    }

    // Métodos auxiliares para manejar la ruta
  /*  public void addMarker(Marker marker) {
        this.routeMarkers.add(marker);
    }*/

    public void removeMarker(StepMarker marker) {
        this.markerConnections.remove(marker);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RouteMarker that = (RouteMarker) o;
        return Objects.equals(routeId, that.routeId) && Objects.equals(path, that.path) && Objects.equals(map, that.map) && Objects.equals(markerConnections, that.markerConnections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(routeId, path, map, markerConnections);
    }
}
