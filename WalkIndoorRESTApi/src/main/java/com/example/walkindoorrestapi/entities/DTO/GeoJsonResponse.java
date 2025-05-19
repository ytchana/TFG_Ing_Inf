package com.example.walkindoorrestapi.entities.DTO;

import com.example.walkindoorrestapi.entities.Intersection;
import com.example.walkindoorrestapi.entities.PointEntity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class GeoJsonResponse {
    private Long id;
    private Integer mapId;
    private Intersection startPoint;
    private PointEntity endPoint;
    private String geoJsonPath;
    private Float direction;
    private Float cost;
    private java.util.Date createdAt;
}

