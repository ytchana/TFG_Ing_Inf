package com.example.walkindoorrestapi.entities.DTO;

import java.util.Objects;

public class ConnectionsRequestDTO {
    private Long startPointId;
    private Long endPointId;
    private Long mapId;
    private Float direction;
    private Float cost;

    // Constructor

    public ConnectionsRequestDTO(Long startPointId, Long endPointId, Long mapId, Float direction, Float cost) {
        this.startPointId = startPointId;
        this.endPointId = endPointId;
        this.mapId = mapId;
        this.direction = direction;
        this.cost = cost;
    }

    public ConnectionsRequestDTO() {
    }
    // Getters y setters

    public Long getStartPointId() {
        return startPointId;
    }

    public void setStartPointId(Long startPointId) {
        this.startPointId = startPointId;
    }

    public Long getEndPointId() {
        return endPointId;
    }

    public void setEndPointId(Long endPointId) {
        this.endPointId = endPointId;
    }

    public Long getMapId() {
        return mapId;
    }

    public void setMapId(Long mapId) {
        this.mapId = mapId;
    }

    public Float getDirection() {
        return direction;
    }

    public void setDirection(Float direction) {
        this.direction = direction;
    }

    public Float getCost() {
        return cost;
    }

    public void setCost(Float cost) {
        this.cost = cost;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionsRequestDTO that = (ConnectionsRequestDTO) o;
        return Objects.equals(startPointId, that.startPointId) && Objects.equals(endPointId, that.endPointId) && Objects.equals(mapId, that.mapId) && Objects.equals(direction, that.direction) && Objects.equals(cost, that.cost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPointId, endPointId, mapId, direction, cost);
    }
}

