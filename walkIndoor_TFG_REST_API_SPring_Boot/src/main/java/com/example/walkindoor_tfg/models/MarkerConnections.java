package com.example.walkindoor_tfg.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * tabla intermedia entre routes y markers
 */
@Entity
@Table(name = "route_markers")
public class MarkerConnections {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private RouteMarker route;

    @ManyToOne
    @JoinColumn(name = "marker_id", nullable = false)
    private StepMarker marker;

    @Column(nullable = false)
    private Integer orderInRoute; // Posición del marcador dentro de la ruta

    @Column(nullable = false)
    private LocalDateTime addedAt; // Fecha de incorporación del marcador en la ruta

    public MarkerConnections() {}

    public MarkerConnections(RouteMarker route, StepMarker marker, Integer orderInRoute, LocalDateTime addedAt) {
        this.route = route;
        this.marker = marker;
        this.orderInRoute = orderInRoute;
        this.addedAt = addedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MarkerConnections that = (MarkerConnections) o;
        return Objects.equals(id, that.id) && Objects.equals(route, that.route) && Objects.equals(marker, that.marker) && Objects.equals(orderInRoute, that.orderInRoute) && Objects.equals(addedAt, that.addedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, route, marker, orderInRoute, addedAt);
    }
}
