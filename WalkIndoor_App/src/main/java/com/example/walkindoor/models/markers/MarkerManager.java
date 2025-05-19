package com.example.walkindoor.models.markers;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.walkindoor.models.entities.Point;
import com.example.walkindoor.models.entities.Route;
import com.example.walkindoor.models.enumerations.FlagType;

import java.util.ArrayList;
import java.util.List;


/**
 * Esta clase gestiona la lógica relacionada con los marcadores en el mapa.
 * Permite agregar, actualizar y recuperar marcadores con notas personalizadas.
 */
public class MarkerManager {

    // Lista que almacena los marcadores
    private final List<Point> markers; // Lista ordenada de marcadores (pasos detectados del usuario)

    // Distancia entre pasos/marcadores en píxeles
    private final int stepDistance;
    private final Context context;

    // Dimensiones del mapa para evitar que los marcadores salgan de los límites
    private int mapWidth = 0;
    private int mapHeight = 0;

    /**
     * Constructor de 'MarkerManager'.
     *
     * @param stepDistance Distancia entre pasos/marcadores en píxeles.
     */
    public MarkerManager(Context context, int stepDistance) {
        this.context = context; //
        this.stepDistance = stepDistance; // Asegura que la distancia entre marcadores no sea nula
        this.markers = new ArrayList<>(); // Asegura que la lista no sea nula
        loadMarkersFromStorage();
    }

    /**
     * Establece las dimensiones del mapa para asegurar que los marcadores estén dentro de los límites.
     * En efecto, el mapa que se utiliza aquí proviene de la clase Canvas que proporciona Android.
     * El lienzo de este mapa tiene dimensiones muy limitadas, que en general no cubrirán todo el entorno que el usuario pretende visitar.
     * @param width  Ancho del mapa.
     * @param height Alto del mapa.
     */
    public void setMapDimensions(int width, int height) {
        if (width > mapWidth) mapWidth = width + stepDistance;
        if (height > mapHeight) mapHeight = height + stepDistance;
        Log.d("MarkerManager", "Mapa expandido dinámicamente: Nuevo ancho=" + mapWidth + ", Nuevo alto=" + mapHeight);

        this.mapWidth = width;
        this.mapHeight = height;
        System.out.println("Dimensiones del mapa actualizadas: Ancho = " + width + ", Alto = " + height);
    }

    /**
     * Agrega un nuevo marcador basado en el rumbo y con una nota opcional.
     * El marcador se calcula en relación al último marcador añadido y se restringe dentro de los límites del mapa.
     *
     * @param azimuth Rumbo actual del dispositivo en grados.
     * @param note    Nota asociada al marcador.
     */
    public void addMarker(float azimuth, String note, FlagType flagType) {
        if (mapWidth == 0 || mapHeight == 0) {
            Log.e("MarkerManager", "Dimensiones del mapa no definidas. No se pueden agregar marcadores.");
            return; // Evita agregar marcadores si el mapa no está inicializado
        }

        // Obtiene la última posición de marcador o establecer la inicial
        int lastX = markers.isEmpty() ? mapWidth / 2 : (int) markers.get(markers.size() - 1).getX();
        int lastY = markers.isEmpty() ? mapHeight / 2 : (int) markers.get(markers.size() - 1).getY();

        // Calcula la nueva posición basada en el rumbo
        int newX = (int) (lastX + stepDistance * Math.sin(Math.toRadians(azimuth)));
        int newY = (int) (lastY - stepDistance * Math.cos(Math.toRadians(azimuth)));

        // Ajusta la posición dentro de los límites del mapa
        newX = Math.max(-stepDistance, Math.min(newX, mapWidth + stepDistance));
        newY = Math.max(-stepDistance, Math.min(newY, mapHeight + stepDistance));

        // Comprobar si ya hay un marcador en esa posición para evitar duplicados
        for (Point marker : markers) {
            if (marker.getX() == newX || marker.getY() == newY) {
                Log.w("MarkerManager", "Cruze de marcadores detectado en X=" + newX + ", Y=" + newY + ". No se añadirá.");
                return; // Evita agregar marcadores duplicados
            }
        }

        // Cada vez que hay un nuevo paso, sus datos se añaden a la lista
        Point newMarker = new Point(newX, newY, note, flagType);
        markers.add(newMarker);

        // Esto sirve para la depuración
        Log.d("MarkerManager", "Nuevo marcador añadido en: X=" + newX + ", Y=" + newY + ", Nota=" + note + ", Bandera=" + flagType);
        System.out.println("MarkerManager: Total de marcadores en la lista después de añadir: " + markers.size());
        System.out.println("Total de marcadores en la lista: " + markers);
        System.out.println("Lista de los marcadores: " + getMarkers());

        /// //////////
        saveMarkersToStorage();
    }

    /**
     * Obtiene el último marcador añadido.
     *
     * @return El último marcador, o `null` si no hay marcadores.
     */
    public Point getLastMarker() {
        if (markers.isEmpty()) {
            Log.e("MarkerManager", "Error: No hay marcadores almacenados.");
            return null;
        }
        return markers.get(markers.size() - 1);
    }

    /**
     * Devuelve la lista completa de marcadores en el mapa.
     *
     * @return Lista de puntos que representan los marcadores.
     */
    public List<Point> getMarkers() {
        return markers;
    }

    /**
     * Obtiene la nota asociada a un marcador específico.
     * Este método será útil cuando se recupere un mapa guardado.
     *
     * @param index Índice del marcador en la lista.
     * @return Nota asociada al marcador, o `null` si no hay nota.
     */
    public String getMarkerNote(int index) {
        if (index >= 0 && index < markers.size()) {
            return markers.get(index).getNote();
        }
        return null;
    }

    /**
     * Obtiene el ancho del mapa.
     *
     * @return Ancho del mapa.
     */
    public int getMapWidth() {
        return mapWidth;
    }

    /**
     * Obtiene el alto del mapa.
     *
     * @return Alto del mapa.
     */
    public int getMapHeight() {
        return mapHeight;
    }

    /**
     * Busca un marcador cercano a una coordenada específica.
     *
     * @param touchX Coordenada X del toque.
     * @param touchY Coordenada Y del toque.
     * @return El marcador tocado, o `null` si no hay ninguno cerca.
     */
    public Point getMarkerAt(float touchX, float touchY) {
        for (Point marker : markers) {
            float distance = (float) Math.sqrt(Math.pow(marker.getX() - touchX, 2) + Math.pow(marker.getY() - touchY, 2));
            if (distance < (float) stepDistance / 2) { // Si el toque está dentro de (float) stepDistance / 2 píxeles del marcador.
                // De momento se deja así, pero habrá que ajustarlo en función de la separación entre pasos.
                // Ya que ahora para el prototipo, parece que cuesta encontrar el toque.
                return marker;
            }
        }
        return null;
    }

    public float getStepDistance() {
        return stepDistance;
    }

    public List<Route> getRoutes() {
        List<Route> routes = new ArrayList<>();
        List<Point> markers = getMarkers(); // Obtiene los marcadores actuales

        if (markers.size() < 2) return routes; // Necesitamos al menos dos puntos para una ruta

        for (int i = 1; i < markers.size(); i++) {
            Point start = markers.get(i - 1);
            Point end = markers.get(i);

            // Crea un objeto Route conectando los marcadores
            routes.add(new Route("SRID=4326;LINESTRING(" + start.getX() + " " + start.getY() + ", " + end.getX() + " " + end.getY() + ")"));
        }

        return routes;
    }


    /// ////////////////////////

    /**
     * Guarda los marcadores en almacenamiento interno.
     * Se trata de un almacenamiento interno temporal antes de enviarlos al Backend:
     * -
     */
    private void saveMarkersToStorage() {
        SharedPreferences prefs = context.getSharedPreferences("MarkerPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        StringBuilder sb = new StringBuilder();
        for (Point marker : markers) {
            sb.append(marker.getX()).append(",").append(marker.getY()).append(";");
        }

        editor.putString("savedMarkers", sb.toString());
        editor.apply();
        Log.d("MarkerManager", "Marcadores guardados en almacenamiento.");
    }



    /**
     * Carga los marcadores guardados al iniciar la aplicación.
     */
    private void loadMarkersFromStorage() {
        SharedPreferences prefs = context.getSharedPreferences("MarkerPrefs", Context.MODE_PRIVATE);
        String savedMarkers = prefs.getString("savedMarkers", "");

        if (!savedMarkers.isEmpty()) {
            String[] markersArray = savedMarkers.split(";");
            for (String markerData : markersArray) {
                String[] coords = markerData.split(",");
                if (coords.length == 2) {
                    float x = Float.parseFloat(coords[0]);
                    float y = Float.parseFloat(coords[1]);
                    markers.add(new Point(x, y, null, null));
                }
            }
        }
        Log.d("MarkerManager", "Total de marcadores cargados desde almacenamiento: " + markers.size());
    }

    public void addMarker2(Point marker) {
        if (marker != null) {
            markers.add(marker);
            Log.d("MarkerManager", "Marcador añadido correctamente: " + marker);
        } else {
            Log.e("MarkerManager", "Intento de agregar un marcador nulo.");
        }
    }


    /**
     * Fuentes consultadas:
     * Cómo guardar datos simples con SharedPreferences: https://developer.android.com/training/data-storage/shared-preferences?hl=es-419#java
     *
     * Shared Preferences in Android with Example: https://www.geeksforgeeks.org/shared-preferences-in-android-with-examples/
     *
     *
     */

}
