package com.example.walkindoor.models.markers;

import android.app.AlertDialog;
import android.util.Log;

import com.example.walkindoor.models.entities.Point;
import com.example.walkindoor.mapsManager.CustomMapView;

public class MarkerUtils {
    private final CustomMapView customMapView;
    // Constructor
    public MarkerUtils(CustomMapView customMapView) {
        this.customMapView = customMapView;
    }

    /**
     * Muestra un cuadro de diálogo con botones para mover un marcador manualmente.
     *
     * @param marker Marcador seleccionado.
     */
    public void showMoveMarkerDialog(Point marker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(customMapView.getContext());
        builder.setTitle("Mover marcador");

        builder.setCancelable(false); // Evita cierre accidental

        builder.setPositiveButton("Arriba", (dialog, which) -> {
            updateMarkerPosition(marker, (int) marker.getX(), (int) (marker.getY() - 20));
            customMapView.invalidate();
        });

        builder.setNegativeButton("Abajo", (dialog, which) -> {
            updateMarkerPosition(marker, (int) marker.getX(), (int) (marker.getY() + 20));
            customMapView.invalidate();
        });

        builder.setNeutralButton("Izquierda", (dialog, which) -> {
            updateMarkerPosition(marker, (int) (marker.getX() - 20), (int) marker.getY());
            customMapView.invalidate();
        });

        builder.setNeutralButton("Derecha", (dialog, which) -> {
            updateMarkerPosition(marker, (int) (marker.getX() + 20), (int) marker.getY());
            customMapView.invalidate();
        });

        builder.show();
    }

    /**
     * Actualiza la posición de un marcador dado.
     *
     * @param marker   El marcador que se desea mover.
     * @param newX     Nueva coordenada X.
     * @param newY     Nueva coordenada Y.
     */
    public void updateMarkerPosition(Point marker, int newX, int newY) {
        marker.setX(Math.max(0, Math.min(newX, new MarkerManager(customMapView.getContext(), 20).getMapWidth()))); // Restringir dentro de los límites
        marker.setY(Math.max(0, Math.min(newY, new MarkerManager(customMapView.getContext(), 20).getMapHeight()))); // Restringir dentro de los límites
        System.out.println("Posición del marcador actualizada: x = " + marker.getX() + ", y = " + marker.getY());
    }

    /**
     * Muestra opciones para editar un marcador: agregar una nota o moverlo.
     * Normalmente este sistema está diseñado para que el usuario solo añada notas y bandera en el último paso (es decir, donde en teoría se encuentra en ese momento.)
     * Pero dada la rapidez con la que a veces los marcadores se dibujan en el mapa, este método es una alternativa manual para añadir una nota o bandera sobre un paso anterior.
     *
     * @param marker Marcador seleccionado.
     */
    public void showMarkerOptionsDialog(Point marker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(customMapView.getContext());
        builder.setTitle("Opciones de marcador");

        // Opción de añadir o modificar la nota del marcador
        builder.setPositiveButton("Añadir Nota", (dialog, which) -> {
            customMapView.showNoteDialog(marker);
        //    showMoveMarkerDialog(marker);
            customMapView.invalidate();
        });

        // Opción de añadir o modificar la bandera del marcador
        builder.setNegativeButton(marker.getFlagType() != null ? "Quitar Bandera" : "Añadir Bandera", (dialog, which) -> {
            Log.d("DialogManager", "Bandera cambiada: " + marker.getFlagType());
            customMapView.showFlagSelectionDialog(marker);
            customMapView.invalidate();
        });

        // Opción de mover el marcador manualmente
        builder.setNeutralButton("Mover Marcador", (dialog, which) -> {
            showMoveMarkerDialog(marker);
        });

        builder.setNeutralButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
}
