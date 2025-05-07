package com.example.walkindoor_tfg.models;

/**
 * Cada Route se representa como una arista con un peso basado en la distancia.
 */
public class EdgeMap {
    public final StepMarker to;
    public final double distance;

    public EdgeMap(StepMarker to, double distance) {
        this.to = to;
        this.distance = distance;
    }
}
