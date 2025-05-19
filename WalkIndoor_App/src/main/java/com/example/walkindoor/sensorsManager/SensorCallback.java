package com.example.walkindoor.sensorsManager;


/**
 * Interfaz que permite la comunicación entre la clase 'SensorHandler' y otras clases.
 * Se usa para actualizar el rumbo y detectar pasos en `MapController`.
 */
public interface SensorCallback {
    void onAzimuthUpdated(float azimuth); // Se llama cuando cambia el rumbo del usuario
    void onStepDetected(); // Se llama cuando se detecta un paso válido
}
