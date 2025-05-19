package com.example.walkindoorrestapi.entities;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.Objects;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "maps")
public class Map {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    @NotBlank(message = "El nombre del mapa no puede estar vacío.")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "La descripción no puede estar vacía.")
    private String description;


    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isPublic;

 /*   @JsonCreator
    public Map(
            @JsonProperty("owner") User owner,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("isPublic") Boolean isPublic
    ) {
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
    }*/

    @JsonCreator
    public Map(
            @JsonProperty("id") Long id,
            @JsonProperty("owner") User owner,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("isPublic") Boolean isPublic
    ) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
    }


    public Map() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Map map = (Map) o;
        return Objects.equals(id, map.id) && Objects.equals(owner, map.owner) && Objects.equals(name, map.name) && Objects.equals(description, map.description) && Objects.equals(isPublic, map.isPublic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, owner, name, description, isPublic);
    }
}

