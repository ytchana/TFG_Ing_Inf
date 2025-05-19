package com.example.walkindoor.mapsManager;



import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import com.example.walkindoor.models.markers.MarkerManager;
import com.example.walkindoor.models.entities.Point;
import com.example.walkindoor.models.entities.Route;

import java.util.List;

/**
 * Esta Clase se encarga de dibujar los elementos visuales del mapa, incluyendo los marcadores,
 * notas y la brújula que refleja el rumbo del usuario. También ajusta el desplazamiento dinámico.
 */

public class MapRenderer {

    // Constantes para tamaños y escalado
    private static final float BASE_MARKER_SIZE = 15.0f; // Tamaño de los marcadores
    private static final float BASE_COMPASS_RADIUS = 100.0f;
    private static final float BASE_TEXT_SIZE = 50.0f;
    private static final double MAX_OFFSET_X = 5000;
    private static final double MAX_OFFSET_Y = 5000;

    // Pinturas para dibujar elementos gráficos
    private final Paint paintMarker;
    private final Paint paintFlag;
    private final Paint routePaint; // para pintar las rutas
    private final Paint paintCompassCircle;
    private final Paint paintCompassNeedle;
    private final Paint paintCompassText;
    private final Paint paintCloseMarker; // Para marcadores cercanos

    // Gestor de marcadores
    private final MarkerManager markerManager;

    // Desplazamiento dinámico y zoom
    private float offsetX = 0;
    private float offsetY = 0;
    private float MAX_SCALE = 5.0f; // Aumenta el zoom máximo para visualizar más terreno
    private float MIN_SCALE = 0.5f; // Reduce el zoom mínimo para permitir una vista más amplia
    private float scaleFactor = 1.0f; // Factor de zoom inicial

    // Rumbo actual (orientación del usuario reflejada en la brújula)
    private float azimuth = 0;

    /**
     * Constructor para inicializar estilos de dibujo y referencia al gestor de marcadores.
     *
     * @param markerManager Instancia de `MarkerManager` para gestionar los marcadores.
     */
    public MapRenderer(MarkerManager markerManager) {
        this.markerManager = markerManager;

        // Configuración de estilos y colores de las pinturas
        paintMarker = new Paint();
        paintMarker.setColor(Color.BLUE); // Marcadores en azul
        paintMarker.setStyle(Paint.Style.FILL);

        paintFlag = new Paint();
        paintFlag.setColor(Color.RED); // Banderas en rojo
        paintFlag.setStyle(Paint.Style.FILL);

        // Configuración del pincel para pintar las rutas
        routePaint = new Paint();
        routePaint.setColor(0xFF0077CC); // Azul oscuro
        routePaint.setStrokeWidth(8);
        routePaint.setStyle(Paint.Style.STROKE);

        paintCompassCircle = new Paint();
        paintCompassCircle.setColor(Color.LTGRAY); // Brújula en gris claro
        paintCompassCircle.setStyle(Paint.Style.FILL);

        paintCompassNeedle = new Paint();
        paintCompassNeedle.setColor(Color.RED); // Aguja de la brújula en rojo
        paintCompassNeedle.setStrokeWidth(8);

        paintCompassText = new Paint();
        paintCompassText.setColor(Color.BLACK); // Texto de brújula en negro
        paintCompassText.setTextSize(BASE_TEXT_SIZE);
        paintCompassText.setTextAlign(Paint.Align.CENTER);

        paintCloseMarker = new Paint();
        paintCloseMarker.setColor(Color.GREEN);  // Pinta de verde el marcador cuando se ha cruzado con otro en el camino. Esto sirve para identificar posibles redes y reconstruir caminos más cortos.
        paintCloseMarker.setStyle(Paint.Style.FILL);
    }

    /**
     * Método principal para renderizar todos los elementos visuales del mapa.
     *
     * @param canvas Canvas donde se dibujan los elementos.
     */
    public void draw(Canvas canvas) {
        drawMarkers(canvas); // Dibuja los marcadores y sus notas y banderas
        drawCompass(canvas); // Dibuja la brújula
        drawRoutes(canvas); // Dibuja las rutas (las conexiones entre los pasos)
    }

    /**
     * Dibuja los marcadores en el mapa, ajustando su posición según el desplazamiento y zoom.
     *
     * @param canvas Canvas donde se dibujan los marcadores.
     */
    private void drawMarkers(Canvas canvas) {
        if (markerManager.getMarkers().isEmpty()) {
            Log.d("MapRenderer", "No hay marcadores para dibujar.");
            return; // Evita ejecución innecesaria si no hay marcadores
        }
        Point previousMarker = null;

        for (Point point : markerManager.getMarkers()) {
            float adjustedX = (point.getX() + offsetX) * scaleFactor;
            float adjustedY = (point.getY() + offsetY) * scaleFactor;
            float markerSize = BASE_MARKER_SIZE * scaleFactor; // Ajustar tamaño con zoom

            Log.d("MapRenderer", "Dibujando marcador en: X=" + adjustedX + ", Y=" + adjustedY);

            // Dibuja el marcador como un círculo
            Paint paint = point.getFlagType() !=null ? paintFlag : paintMarker;
            // Si hay un marcador anterior, calcular distancia y cambiar estilo si es menor a 'stepDistance'
            if (previousMarker != null) {
                float distance = (float) Math.sqrt(
                        Math.pow(previousMarker.getX() - point.getX(), 2) + Math.pow(previousMarker.getY() - point.getY(), 2)
                );

                if (Math.abs(point.getX() - previousMarker.getX()) <  0.0001 || Math.abs(point.getY() - previousMarker.getY()) < 0.0001) {
                    paint = paintCloseMarker; // Cambiar a color verde
                    canvas.drawRect(adjustedX - markerSize, adjustedY - markerSize, adjustedX + markerSize, adjustedY + markerSize, paint);
                } else {
                    canvas.drawCircle(adjustedX, adjustedY, markerSize, paint);
                }
            } else {
                canvas.drawCircle(adjustedX, adjustedY, markerSize, paint);
            }

            previousMarker = point; // Guarda el marcador actual para la siguiente iteración

            // Dibujar la nota del marcador si existe
            if (point.getNote() != null && !point.getNote().isEmpty()) {
                paintCompassText.setTextSize(BASE_TEXT_SIZE * scaleFactor);
                // Dibujar la nota pegada al marcador
                canvas.drawText(point.getNote(), adjustedX + 10 * scaleFactor, adjustedY - 10 * scaleFactor, paintCompassText);
            }
            // Dibujar la bandera del marcador si existe
            if (point.getFlagType() != null) {
                paintCompassText.setTextSize(BASE_TEXT_SIZE * scaleFactor);
                // Dibujar la bandera pegada al marcador
                canvas.drawText(point.getFlagType().toString(), adjustedX + 10 * scaleFactor, adjustedY - 10 * scaleFactor, paintCompassText);
            }
        }
    }

    /**
     * Dibuja una brújula en la esquina inferior derecha del mapa.
     * La brújula refleja el rumbo actual del usuario.
     *
     * @param canvas Canvas donde se dibuja la brújula.
     */
    private void drawCompass(Canvas canvas) {
        // Obtener dimensiones dinámicas del mapa
        int mapWidth = markerManager.getMapWidth();
        int mapHeight = markerManager.getMapHeight();

        // Posicionar la brújula en la esquina inferior derecha
        float centerX = (mapWidth - 150) * scaleFactor; // Ajustar según el zoom
        float centerY = (mapHeight - 150) * scaleFactor;
        float radius = BASE_COMPASS_RADIUS * scaleFactor; // Escalar el radio de la brújula

        // Dibujar el círculo de la brújula
        canvas.drawCircle(centerX, centerY, radius, paintCompassCircle);

        // Dibujar los puntos cardinales
        paintCompassText.setTextSize(BASE_TEXT_SIZE * scaleFactor); // Escalar tamaño del texto
        canvas.drawText("N", centerX, centerY - radius + (BASE_TEXT_SIZE * scaleFactor), paintCompassText);
        canvas.drawText("S", centerX, centerY + radius - (BASE_MARKER_SIZE * scaleFactor), paintCompassText);
        canvas.drawText("E", centerX + radius - (20 * scaleFactor), centerY + (10 * scaleFactor), paintCompassText);
        canvas.drawText("O", centerX - radius + (20 * scaleFactor), centerY + (10 * scaleFactor), paintCompassText);

        // Dibujar la aguja de la brújula según el rumbo actual
        float needleX = (float) (centerX + radius * Math.sin(Math.toRadians(azimuth)));
        float needleY = (float) (centerY - radius * Math.cos(Math.toRadians(azimuth)));
        canvas.drawLine(centerX, centerY, needleX, needleY, paintCompassNeedle);

        /**
         * Fuentes consultadas:
         * How to make a customized compass view in android: https://stackoverflow.com/questions/15240865/how-to-make-a-customized-compass-view-in-android
         * Simple compass code with Android Studio: https://www.codespeedy.com/simple-compass-code-with-android-studio/
         */
    }

    private void drawRoutes(Canvas canvas) {
        List<Route> routes = markerManager.getRoutes(); // Obtener rutas generadas dinámicamente

        for (Route route : routes) {
            Path path = new Path();
            String[] points = route.getPath().replace("SRID=4326;LINESTRING(", "").replace(")", "").split(",");

            if (points.length < 2) continue; // La ruta necesita al menos 2 puntos

            String[] startCoords = points[0].trim().split(" ");
            float startX = Float.parseFloat(startCoords[0]) * scaleFactor + offsetX;
            float startY = Float.parseFloat(startCoords[1]) * scaleFactor + offsetY;

            path.moveTo(startX, startY);

            for (int i = 1; i < points.length; i++) {
                String[] coords = points[i].trim().split(" ");
                float x = Float.parseFloat(coords[0]) * scaleFactor + offsetX;
                float y = Float.parseFloat(coords[1]) * scaleFactor + offsetY;

                path.lineTo(x, y);
            }

            canvas.drawPath(path, routePaint);
        }
    }

    // Enlace de fuente consultada:
    // https://github.com/AlexandreLouisnard/compass-android/blob/master/app/src/main/java/com/louisnard/compass/CompassView.java

    /**
     * Actualiza el desplazamiento del mapa.
     *
     * @param offsetX Nuevo desplazamiento horizontal.
     * @param offsetY Nuevo desplazamiento vertical.
     */
    public void updateOffset(float offsetX, float offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;

        this.offsetX = (float) Math.max(-MAX_OFFSET_X, Math.min(offsetX, MAX_OFFSET_X));
        this.offsetY = (float) Math.max(-MAX_OFFSET_Y, Math.min(offsetY, MAX_OFFSET_Y));
        System.out.println("Desplazamiento actualizado: offsetX = " + offsetX + ", offsetY = " + offsetY);
    }

    /**
     * Actualiza el rumbo actual del usuario para reflejarlo en la brújula.
     *
     * @param azimuth Nuevo rumbo en grados.
     */
    public void updateAzimuth(float azimuth) {
        this.azimuth = azimuth;
        System.out.println("Rumbo actualizado en brújula: " + azimuth);
    }

    /**
     * Actualiza el factor de zoom para escalar los elementos del mapa.
     *
     * @param scaleFactor Nuevo factor de zoom.
     */
    public void updateScaleFactor(float scaleFactor) {
        this.scaleFactor = Math.max(MIN_SCALE, Math.min(scaleFactor, MAX_SCALE)); // Limitar zoom
        System.out.println("Factor de zoom actualizado: " + scaleFactor);
    }
}

/**
 * Fuentes consultadas:
 *https://github.com/daphnis-services-apps/DTCSkinClinic/blob/4788ea1eb53361c1eb22d78c2069d16aee819aa9/app/src/main/java/com/daphnistech/dtcskinclinic/helper/Signature.java
 *
 */
