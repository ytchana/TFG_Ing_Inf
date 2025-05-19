package com.example.walkindoor.mapsManager;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.app.AlertDialog;
import android.view.ViewGroup;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.walkindoor.R;
import com.example.walkindoor.models.enumerations.FlagType;
import com.example.walkindoor.models.markers.MarkerUtils;
import com.example.walkindoor.sensorsManager.SensorCallback;
import com.example.walkindoor.models.markers.MarkerManager;
import com.example.walkindoor.models.entities.Point;
import com.example.walkindoor.sensorsManager.SensorHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * Esta clase actúa como el contenedor principal del mapa, gestionando la detección de pasos,
 * la actualización del rumbo, la visualización y edición de marcadores, así como el desplazamiento dinámico.
 */
public class CustomMapView extends View implements SensorCallback {

    // Variables: Componentes principales de la vista del mapa
    private final MarkerManager markerManager;        // Control de los marcadores. Se encarga de añadir un marcador en los pasos.
    private final MapController mapController;        // Controlador del mapa (incluye desplazamiento y gestos)
    private final MapRenderer mapRenderer;            // Renderizador del mapa
    private final SensorHandler sensorHandler;        // Manejo de sensores (acelerómetro, magnetómetro, etc.)
    private final MarkerUtils markerUtils;            // Para gestionar las opciones de los marcadores

    private Paint paint;
    private List<Point> markers;
    private int mapWidth, mapHeight;
    private boolean isInitialized = false;
    private FloatingActionButton btnAddNote;

    /**
     * Constructor para inicializar `CustomMapView` desde XML.
     *
     * @param context Contexto de la aplicación.
     * @param attrs   Atributos definidos en el diseño XML.
     */
    public CustomMapView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Inicialización de componentes clave
        markerManager = new MarkerManager(this.getContext(), 40); // Distancia entre marcadores: 40 píxeles
        mapRenderer = new MapRenderer(markerManager);
        mapController = new MapController(context, markerManager, mapRenderer);
        sensorHandler = new SensorHandler(context, this); // Callback implementado en esta clase
        markerUtils = new MarkerUtils(this);
    }

    /**
     * Se llama cuando cambian las dimensiones de la vista.
     *
     * @param w    Nuevo ancho de la vista.
     * @param h    Nuevo alto de la vista.
     * @param oldw Ancho anterior.
     * @param oldh Alto anterior.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        markerManager.setMapDimensions(w, h); // Actualizar las dimensiones del mapa en 'MarkerManager'
    }

    /**
     * Método principal para dibujar la vista y delega la renderización a MapRenderer.
     *
     * @param canvas Canvas donde se dibujan los elementos visuales.
     */
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        mapRenderer.draw(canvas); // Dibuja los marcadores y la brújula
    }


    /**
     * Método llamado al detectar un paso.
     * Se añade un nuevo marcador en la dirección del usuario sin nota inicialmente.
     */
    @Override
    public void onStepDetected() {
        // Ajusta la posición del mapa si el marcador alcanza los bordes
        mapController.onStepDetected();

        // Redibuja la vista para reflejar los cambios
        invalidate();
    }

    /**
     * Método llamado al actualizar el rumbo del usuario.
     *
     * @param azimuth Nuevo rumbo en grados.
     */
    @Override
    public void onAzimuthUpdated(float azimuth) {
        mapController.onAzimuthUpdated(azimuth); // Actualiza el rumbo en 'MapController'
        mapRenderer.updateAzimuth(azimuth); // Actualiza la visualización de la brújula
        invalidate(); // Redibuja la vista
    }

    /**
     * Método para gestionar la interacción del usuario con la pantalla.
     * Se detectan los gestos de desplazamiento y zoom.
     *
     * @param event Evento de toque que proporciona la posición X e Y.
     * @return 'true' si se manejó el evento de toque correctamente.
     */

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Verificar si el controlador del mapa está disponible
        if (mapController == null) {
            return super.onTouchEvent(event);
        }
        // Procesar gestos de zoom y de desplazamiento
        boolean eventHandled = mapController.onTouchEvent(event);

        performClick();

        // Retornar 'true' si al menos uno de los eventos fue manejado
        return handleMarkerTouch(event) || eventHandled || super.onTouchEvent(event);
    }

    // Sobrescribir performClick() para cumplir con el estándar de accesibilidad
    @Override
    public boolean performClick() {
        super.performClick();
        return true; // Indica que el evento de clic ha sido manejado
    }

    // Detectar toque en un marcador
    private boolean handleMarkerTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float touchX = event.getX();
            float touchY = event.getY();

            Point touchedMarker = markerManager.getMarkerAt(touchX, touchY);
            if (touchedMarker != null) {
                // Marcador seleccionado por el usuario (para edición visual)
                markerUtils.showMarkerOptionsDialog(touchedMarker); // Mostrar opciones de edición
                // Llamar a performClick() para cumplir con las reglas de accesibilidad
                return performClick(); // Cumple con las reglas de accesibilidad
            }
        }
        return false;
    }


    /**
     * Método llamado cuando la vista se adjunta a la ventana.
     * Registra los sensores necesarios.
     */
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        sensorHandler.registerSensors(); // Activar sensores de pasos y rumbo

        // Si el botón ya existe, eliminarlo antes de agregarlo nuevamente
        if (btnAddNote != null) {
            ViewGroup parent = (ViewGroup) btnAddNote.getParent();
            if (parent != null) {
                parent.removeView(btnAddNote);
            }
        }

        // Botón alternativo para añadir notas
        // Crear el botón flotante si aún no existe
        btnAddNote = new FloatingActionButton(getContext());
        btnAddNote.setImageResource(R.drawable.ic_add_notes);
        // Ícono de nota
        btnAddNote.setContentDescription("Añadir Nota");

        // Ajusta su posición en el centro abajo, alineado con "Guardar Mapa"
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        layoutParams.setMargins(0, 0, 0, 100); // Espacio adecuado para alinearse con "Guardar Mapa"
        btnAddNote.setLayoutParams(layoutParams);

        // Agrega el botón flotante a la vista raíz
        ViewGroup rootView = (ViewGroup) getRootView();
        if (rootView instanceof FrameLayout) {
            ((FrameLayout) rootView).addView(btnAddNote);
        } else {
            Log.e("CustomMapView", "Error: btnAddNote no puede agregarse a la vista raíz.");
        }

        // Configura la acción del botón
        btnAddNote.setOnClickListener(view -> {
            Point lastMarker = markerManager.getLastMarker();

            if (lastMarker != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Seleccionar acción");

                String[] options = {"Añadir Nota", "Añadir Bandera", "Ambas"};
                builder.setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            showNoteDialog(lastMarker);
                            break;
                        case 1:
                            showFlagSelectionDialog(lastMarker);
                            break;
                        case 2:
                            showNoteDialog(lastMarker);
                            showFlagSelectionDialog(lastMarker);
                            break;
                    }
                });

                builder.show();
            } else {
                Toast.makeText(getContext(), "No hay marcadores disponibles", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Método llamado cuando la vista se separa de la ventana.
     * Desregistra los sensores para ahorrar batería.
     */
    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        sensorHandler.unregisterSensors(); // Desactivar sensores

        // Eliminar el botón cuando la vista se destruye
        if (btnAddNote != null && btnAddNote.getParent() != null) {
            ((ViewGroup) btnAddNote.getParent()).removeView(btnAddNote);
        }
    }

    /**
     * Muestra un cuadro de diálogo para añadir o modificar la nota de un marcador.
     *
     * @param marker Marcador al que se asociará la nota.
     */
    public void showNoteDialog(Point marker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Añadir nota");

        final EditText input = new EditText(getContext());
        input.setText(marker.getNote() != null ? (CharSequence) marker.getNote() : ""); // Mostrar nota existente si hay
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            marker.setNote(input.getText().toString()); // Guardar nota en el marcador
            invalidate(); // Redibujar para reflejar el cambio
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
    // Método para elegir bandera
    public void showFlagSelectionDialog(Point marker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Seleccionar tipo de bandera");

        String[] flagOptions = {"Peligro", "Entrada", "Salida", "Información", "Eliminar Bandera", "Bandera por defecto"};
        builder.setItems(flagOptions, (dialog, which) -> {
            FlagType selectedFlag = null;
            switch (which) {
                case 0:
                    selectedFlag = FlagType.WARNING;
                    break;
                case 1:
                    selectedFlag = FlagType.START;
                    break;
                case 2:
                    selectedFlag = FlagType.END;
                    break;
                case 3:
                    selectedFlag = FlagType.INFO;
                    break;
                case 4:
                    selectedFlag = null; // Eliminar bandera
                    break;
                case 5:
                    selectedFlag = FlagType.DEFAULT;
                    break;
                case 6:
                    selectedFlag = FlagType.CUSTOM;
                    break;
                case 7:
                    selectedFlag = FlagType.LANDMARK;
                    break;
            };

            marker.setFlagType(selectedFlag);
            invalidate();

            Toast.makeText(getContext(), selectedFlag != null ? "Bandera añadida: " + flagOptions[which] : "Bandera eliminada", Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }
}

/**
 * Enlaces de Fuentes consultadas:
 * [1] https://github.com/daphnis-services-apps/DTCSkinClinic/blob/4788ea1eb53361c1eb22d78c2069d16aee819aa9/app/src/main/java/com/daphnistech/dtcskinclinic/helper/Signature.java
 * [2] https://github.com/jdhenckel/android-redball/blob/b5cd9d8693e209836d2e87349e5155cb96b393b9/app/src/main/java/com/example/helloandroid/MainActivity.java
 * [3] https://github.com/jdhenckel/android-redball/blob/b5cd9d8693e209836d2e87349e5155cb96b393b9/app/src/main/java/com/example/helloandroid/MainActivity.java
 * [4] https://github.com/jhelbrink/MAMN01_MySensorApp/blob/765e305fef25221e988f93e63ba684f3ff87e319/MySensorApp/app/src/main/java/com/example/john/mysensorapp/Accelerometer.java
 * [5]
 *
 *
 *
 *
 */
