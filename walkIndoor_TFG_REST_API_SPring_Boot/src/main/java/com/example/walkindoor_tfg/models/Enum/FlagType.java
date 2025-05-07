package com.example.walkindoor_tfg.models.Enum;

public enum FlagType {
    DEFAULT("Valor por defecto"),
    WARNING("Zona peligrosa"),
    START("Punto de entrada"),
    END("Punto de salida"),
    INFO("Información útil"),
    LANDMARK("..."),
    CUSTOM("...");

    private final String description;

    FlagType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}