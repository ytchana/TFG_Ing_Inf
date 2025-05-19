package com.example.walkindoor.mapsManager;


import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.content.Context;

import androidx.annotation.NonNull;

import com.example.walkindoor.models.entities.Route;
import com.example.walkindoor.sensorsManager.SensorCallback;
import com.example.walkindoor.models.markers.MarkerManager;
import com.example.walkindoor.models.entities.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase gestiona el desplazamiento del mapa en función de los pasos detectados y el rumbo del usuario.
 * Se encarga de ajustar la vista cuando los marcadores alcanzan los bordes del mapa.
 */
public class MapController implements SensorCallback {

    private float azimuth = 0; // Dirección actual del usuario en grados
    private float offsetX = 0;   // Desplazamiento horizontal del mapa
    private float offsetY = 0;   // Desplazamiento vertical del mapa
    private float scaleFactor = 1.0f; // Factor de escala para el zoom

    private final MarkerManager markerManager; // Gestión de marcadores
    private final MapRenderer mapRenderer;     // Renderización del mapa

    private final List<Route> routeList = new ArrayList<>(); // Almacena las rutas

    private final GestureDetector gestureDetector; // Detector de gestos de desplazamiento
    private final ScaleGestureDetector scaleGestureDetector; // Detector de gestos de zoom

    // Límites para el zoom
    private static final float MIN_SCALE = 0.5f; // Mínimo nivel de zoom
    private static final float MAX_SCALE = 3.0f; // Máximo nivel de zoom

    /**
     * Constructor para inicializar el controlador del mapa con soporte táctil.
     *
     * @param context       Contexto de la aplicación.
     * @param markerManager Instancia de 'MarkerManager' para gestionar los marcadores.
     * @param mapRenderer   Instancia de 'MapRenderer' para actualizar la visualización del mapa.
     */
    public MapController(Context context, MarkerManager markerManager, MapRenderer mapRenderer) {
        this.markerManager = markerManager;
        this.mapRenderer = mapRenderer;

        // Inicializar detectores de gestos
        gestureDetector = new GestureDetector(context, new GestureListener());
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    /**
     * Método llamado cuando el rumbo del usuario cambia.
     * Actualiza el rumbo en grados y redibuja la brújula.
     *
     * @param azimuth Nuevo rumbo en grados.
     */
    @Override
    public void onAzimuthUpdated(float azimuth) {
        this.azimuth = azimuth;
        mapRenderer.updateAzimuth(azimuth); // Actualizar visualización en la brújula
        System.out.println("Rumbo actualizado en MapController: " + azimuth);
    }

    /**
     * Método llamado cuando se detecta un paso.
     * Añade un nuevo marcador al mapa y ajusta el desplazamiento si el marcador alcanza los bordes.
     * Añade un marcador y actualiza la ruta.
     *
     */
    @Override
    public void onStepDetected() {
        // Añadir un nuevo marcador según el rumbo del usuario
        markerManager.addMarker(azimuth, null, null);
        Log.d("MarkerManager", "Total de marcadores almacenados: " + markerManager.getMarkers().size());


        // Ajustar la posición del mapa para mantener los marcadores visibles
        adjustMapPositionSmoothly();
        generateRoutes(); // Generar rutas después de cada nuevo paso
    }

    /**
     * Ajusta el desplazamiento del mapa de manera suave en función de la posición de los marcadores.
     */
    private void adjustMapPositionSmoothly() {
        Point lastMarker = markerManager.getLastMarker();
        if (lastMarker == null) return;

        int mapWidth = markerManager.getMapWidth();
        int mapHeight = markerManager.getMapHeight();

        // Obtener las coordenadas del marcador y definir los límites
        float targetX = offsetX;
        float targetY = offsetY;
        int markerX = (int) lastMarker.getX();
        int markerY = (int) lastMarker.getY();

        // Límite de margen para ajustar el desplazamiento (distancia mínima antes de mover el mapa)
        final int MARGIN = 150;

        if (markerX >= mapWidth - MARGIN) {
            targetX -= MARGIN;  // Desplazar el mapa hacia la izquierda
        } else if (markerX <= MARGIN) {
            targetX += MARGIN;  // Desplazar el mapa hacia la derecha
        }

        if (markerY >= mapHeight - MARGIN) {
            targetY -= MARGIN;  // Desplazar el mapa hacia arriba
        } else if (markerY <= MARGIN) {
            targetY += MARGIN;  // Desplazar el mapa hacia abajo
        }

        // Usar interpolación para un movimiento más fluido
        offsetX = interpolate(offsetX, targetX, 0.1f);
        offsetY = interpolate(offsetY, targetY, 0.1f);

        mapRenderer.updateOffset(offsetX, offsetY);
        Log.d("MapController", "Mapa ajustado dinámicamente: offsetX=" + offsetX + ", offsetY=" + offsetY);
    }

    /**
     * Método de interpolación para movimientos suaves.
     *
     * @param current Valor actual.
     * @param target  Valor objetivo.
     * @param factor  Velocidad de transición (0 = sin movimiento, 1 = cambio inmediato).
     * @return Nuevo valor interpolado.
     */
    private float interpolate(float current, float target, float factor) {
        return current + (target - current) * factor;
    }

    /**
     * Maneja los gestos táctiles del usuario, incluyendo desplazamiento y zoom.
     *
     * @param event Evento táctil.
     * @return `true` si el evento se procesó correctamente.
     */
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event); // Procesar zoom
        gestureDetector.onTouchEvent(event); // Procesar desplazamiento
        return true;
    }

    /**
     * Clase interna para manejar gestos de desplazamiento con un dedo.
     * Esto permite al usuario mover el mapa con un dedo
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent var1, @NonNull MotionEvent var2, float distanceX, float distanceY) {
            offsetX -= distanceX; // Ajustar desplazamiento en X
            offsetY -= distanceY; // Ajustar desplazamiento en Y

            // Limitar desplazamiento para evitar bordes invisibles
            offsetX = Math.max((float) - markerManager.getMapWidth() / 2, Math.min(offsetX, (float) markerManager.getMapWidth() / 2));
            offsetY = Math.max((float) - markerManager.getMapHeight() / 2, Math.min(offsetY, (float) markerManager.getMapHeight() / 2));

            mapRenderer.updateOffset(offsetX, offsetY); // Actualizar desplazamiento visual
            return true;
        }

        /**
         * Fuentes consultadas:
         * Detecting gestures on Android via GestureDetector: https://en.proft.me/2017/06/25/detecting-gestures-android-gesturedetector/
         *
         * Scroll, Press, and Tap: A Guide of Android Gesture Detection: https://medium.com/@robinhoo990512/scroll-press-and-tap-a-guide-of-android-gesture-detection-eb63104c526c
         * Crea una implementación personalizada de desplazamiento táctil: https://developer.android.com/develop/ui/views/touch-and-input/gestures/scroll?hl=es-419
         */
    }

    /**
     * Clase interna para manejar gestos de zoom con dos dedos.
     */
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor(); // Ajustar factor de escala
            scaleFactor = Math.max(MIN_SCALE, Math.min(scaleFactor, MAX_SCALE)); // Limitar zoom
            mapRenderer.updateScaleFactor(scaleFactor); // Actualizar escala visual
            return true;
        }
    }

    /**
     * Obtiene el desplazamiento horizontal actual del mapa.
     *
     * @return Valor de desplazamiento horizontal.
     */
    public float getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(float x) {
        this.offsetX = x;
    }

    /**
     * Obtiene el desplazamiento vertical actual del mapa.
     *
     * @return Valor de desplazamiento vertical.
     */
    public float getOffsetY() {
        return offsetY;
    }
    public void setOffsetY(float y) {
        this.offsetY = y;
    }

    // Método para obtener todas las rutas actuales en el mapa
    public List<Route> getRoutes() {
        return routeList;
    }

    // Método para agregar una nueva ruta al mapa
    public void addRoute(Route route) {
        routeList.add(route);
    }

    /**
     * Método para construir rutas dinámicamente entre marcadores existentes.
     */
    private void generateRoutes() {
        routeList.clear(); // Limpiar rutas previas

        List<Point> markers = markerManager.getMarkers();
        if (markers.size() < 2) return; // No se puede construir una ruta con menos de dos puntos

        for (int i = 1; i < markers.size(); i++) {
            Point start = markers.get(i - 1);
            Point end = markers.get(i);

            // Crear un objeto Route conectando los puntos
            Route route = new Route("SRID=4326;LINESTRING(" + start.getX() + " " + start.getY() + ", " + end.getX() + " " + end.getY() + ")");
            routeList.add(route);
        }
    }

}
