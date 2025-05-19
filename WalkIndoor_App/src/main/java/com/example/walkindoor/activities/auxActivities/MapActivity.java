package com.example.walkindoor.activities.auxActivities;

import android.animation.ValueAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.walkindoor.R;
import com.example.walkindoor.api.User;
import com.example.walkindoor.models.markers.Marker;
import com.example.walkindoor.models.entities.MapFrontend;
import com.example.walkindoor.models.markers.MarkerManager;
import com.example.walkindoor.models.entities.Point;
import com.example.walkindoor.api.ApiService;
import com.example.walkindoor.api.ApiClient;
import com.example.walkindoor.mapsManager.CustomMapView;
import com.example.walkindoor.mapsManager.MapController;
import com.example.walkindoor.mapsManager.MapRenderer;
import com.example.walkindoor.models.enumerations.FlagType;
import com.example.walkindoor.models.storage.StorageHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



/**
 * 'MapActivity' inicializa y ejecuta 'CustomMapView' para crear y gestionar mapas.
 */

public class MapActivity extends AppCompatActivity {
    // Objetos necesarios
    private CustomMapView customMapView; // El gestor de las vistas
    private MarkerManager markerManager;    // El gestor de los marcadores
    private MapController mapController;    // El gestor del mapa
    private MapRenderer mapRenderer;    // El gestor de la renderización del mapa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_map);
        } catch (Exception e) {
            Log.e("MapActivity", "Error al inicializar MapActivity", e);
        }

        if (markerManager == null) { // para evitar varias inicializaciones
            markerManager = new MarkerManager(this, 10);
        }
        mapRenderer = new MapRenderer(markerManager);
        mapController = new MapController(this, markerManager, mapRenderer);
        customMapView = findViewById(R.id.customMapView);
        if (customMapView == null) {
            Log.e("MapActivity", "Error: customMapView es nulo. Revisa activity_map.xml.");
        } else {
            Log.d("MapActivity", "customMapView inicializado correctamente.");
        }

        // Botón para centrar el mapa en el último marcador
        FloatingActionButton fabCenterMap = findViewById(R.id.fab_center_map);
        if (fabCenterMap == null) {
            Log.e("MapActivity", "fabCenterMap es nulo. Verifica que el ID 'fab_center_map' está definido en el XML.");
            return;
        } else {
            fabCenterMap.setOnClickListener(view -> centerMap());
        }

        // Botón para guardar mapas
        Button btnSaveMap = findViewById(R.id.btnSaveMap);
        btnSaveMap.setOnClickListener(view -> askMapNameAndSave());

        initializeCustomMapView();
    }
    private void askMapNameAndSave() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nombre del mapa");

        final EditText input = new EditText(this);
        input.setHint("Ej: Mapa de mi casa");
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String mapName = input.getText().toString().trim();
            Long userId = getCurrentUserId(); // Asegurar que `getCurrentUserId()` funciona correctamente

            if (userId == -1) {
                Log.e("MapActivity", "Error: No se pudo obtener el ID del usuario.");
                Toast.makeText(MapActivity.this, "No se pudo obtener el ID del usuario", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Log.d("MapActivity", "ID de usuario obtenido correctamente: " + userId);
            }

            if (mapName.isEmpty() || userId == null) {
                Toast.makeText(this, "El nombre del mapa y el usuario no pueden estar vacíos", Toast.LENGTH_SHORT).show();
            } else {
                saveMapToDatabase(mapName, userId);
            }
        });


        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void saveMap(String mapName, Long userId) {
        MapFrontend newMap = new MapFrontend(mapName, new User(userId));

        ApiService apiService = ApiClient.getApiService();
        Call<MapFrontend> call = apiService.createMap(newMap);

        call.enqueue(new Callback<MapFrontend>() {
            @Override
            public void onResponse(@NonNull Call<MapFrontend> call, @NonNull Response<MapFrontend> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MapFrontend savedMap = response.body();
                    Log.d("API", "Mapa guardado con éxito: " + savedMap.getName());
                    Toast.makeText(MapActivity.this, "Mapa guardado correctamente.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("API", "Error al guardar mapa: " + response.code());
                    Toast.makeText(MapActivity.this, "Error al guardar mapa: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<MapFrontend> call, Throwable t) {
                Toast.makeText(MapActivity.this, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Centra el mapa en el último marcador agregado.
     */
    private void centerMap() {
        Point userPosition = markerManager.getLastMarker();

        if (userPosition == null) {
            Log.e("MapActivity", "Error: No hay un marcador disponible para centrar.");
            Toast.makeText(this, "No hay un marcador disponible para centrar.", Toast.LENGTH_SHORT).show();
            return;
        }else {
            float centerX = -userPosition.getX() + (float) markerManager.getMapWidth() / 2;
            float centerY = -userPosition.getY() + (float) markerManager.getMapHeight() / 2;

            mapController.setOffsetX(centerX);
            mapController.setOffsetY(centerY);
            mapRenderer.updateOffset(centerX, centerY);

            Log.d("MapActivity", "Mapa centrado en el marcador actual: X=" + userPosition.getX() + ", Y=" + userPosition.getY());
        }
        animateMapMovement(userPosition.getX(), userPosition.getY());
    }
    /**
     * Animación para mover el mapa suavemente a la nueva posición.
     */
    private void animateMapMovement(float targetX, float targetY) {
        ValueAnimator animatorX = ValueAnimator.ofFloat(mapController.getOffsetX(), targetX);
        ValueAnimator animatorY = ValueAnimator.ofFloat(mapController.getOffsetY(), targetY);

        animatorX.setDuration(500);
        animatorY.setDuration(500);
        animatorX.setInterpolator(new DecelerateInterpolator());
        animatorY.setInterpolator(new DecelerateInterpolator());

        animatorX.addUpdateListener(animation -> {
            mapController.setOffsetX((float) animation.getAnimatedValue());
            mapRenderer.updateOffset(mapController.getOffsetX(), mapController.getOffsetY());
        });

        animatorY.addUpdateListener(animation -> {
            mapController.setOffsetY((float) animation.getAnimatedValue());
            mapRenderer.updateOffset(mapController.getOffsetX(), mapController.getOffsetY());
        });

        animatorX.start();
        animatorY.start();
    }

    private void saveMapToDatabase(String mapName, Long userId) {
        // Verificar que el usuario es válido
        if (userId == null || userId == -1) {
            Toast.makeText(this, "Error: No se pudo obtener el ID del usuario", Toast.LENGTH_SHORT).show();
            return;
        }
        // Crear el objeto Mapa con el nombre y usuario
        User user = new User();
        user.setId(userId);
        MapFrontend mapFrontend = new MapFrontend(mapName, user);
        mapFrontend.setUserId(userId); // Asociar el mapa al usuario

        // Recopilar los marcadores actuales
        List<Marker> markerList = new ArrayList<>();
        for (Point point : markerManager.getMarkers()) {
            Marker marker = new Marker(point.toWKT(), point.getNote(),
                    point.getFlagType() != null ? FlagType.valueOf(point.getFlagType().name()) : null);
            markerList.add(marker);
        }
        mapFrontend.setMarkers(markerList);

        // Enviar el mapa al backend con Retrofit
        ApiService apiService = ApiClient.getApiService();
        Call<MapFrontend> call = apiService.createMap(mapFrontend, userId);

        call.enqueue(new Callback<MapFrontend>() {
            @Override
            public void onResponse(@NonNull Call<MapFrontend> call, @NonNull Response<MapFrontend> response) {
                if (response.isSuccessful()) {
                    MapFrontend savedMap = response.body();
                    assert savedMap != null;
                    Log.d("API", "Mapa guardado con éxito: " + savedMap.getName());

                    // Guardar los marcadores en el backend después de guardar el mapa
                    for (Marker marker : markerList) {
                        saveMarkerToDatabase(savedMap.getId(), marker);
                    }

                    Toast.makeText(MapActivity.this, "Mapa guardado exitosamente.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("API", "Error al guardar mapa: Código " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<MapFrontend> call, @NonNull Throwable t) {
                Log.e("API", "Error de conexión", t);
            }
        });
    }

    private void saveMarkerToDatabase(Long mapId, Marker marker) {
        ApiService apiService = ApiClient.getApiService();
        Call<Marker> call = apiService.addMarker(marker, mapId);

        call.enqueue(new Callback<Marker>() {
            @Override
            public void onResponse(Call<Marker> call, Response<Marker> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API", "Marcador guardado con éxito: " + response.body().getLocation());
                } else {
                    Log.e("API", "Error al guardar marcador: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Marker> call, Throwable t) {
                Log.e("API", "Error de conexión al guardar marcador", t);
            }
        });
    }

    /**
     * Inicializa la vista personalizada del mapa.
     */
    private void initializeCustomMapView() {
        System.out.println("CustomMapView inicializado correctamente.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (customMapView != null) {
            customMapView.onAttachedToWindow();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (customMapView != null) {
            customMapView.onDetachedFromWindow();
        }
        Log.d("MapActivity", "Guardando marcadores antes de salir.");
        guardarMarcadores();
    }

    private void guardarMarcadores() {
        SharedPreferences prefs = getSharedPreferences("MarkerPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        StringBuilder sb = new StringBuilder();
        for (Point marker : markerManager.getMarkers()) {
            sb.append(marker.getX()).append(",").append(marker.getY()).append(",")
                    .append(marker.getNote()).append(",")
                    .append(marker.getFlagType()).append(";");
        }

        String markerData = sb.toString();
        editor.putString("savedMarkers", sb.toString());
        editor.apply();

        // Guardar en archivo .txt
        StorageHelper.saveToFile(this, "markers.txt", markerData);
        Log.d("MapActivity", "Marcadores guardados en archivo.");
    }

    private void cargarMarcadores() {
        String savedData = StorageHelper.readFromFile(this, "markers.txt");
        Log.d("MapActivity", "Marcadores cargados: " + savedData);

        if (savedData == null || savedData.isEmpty()) {
            Toast.makeText(this, "No hay marcadores guardados", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] markers = savedData.split(";");
        for (String markerString : markers) {
            String[] data = markerString.split(",");
            if (data.length == 4) { // Verificar que todos los datos están presentes
                Point marker = new Point(Float.parseFloat(data[0]), Float.parseFloat(data[1]));
                marker.setNote(data[2]);
                try {
                    marker.setFlagType(FlagType.valueOf(data[3])); // Convertir String a FlagType
                } catch (IllegalArgumentException e) {
                    Log.e("MarkerManager", "Error al convertir flagType: " + data[3]);
                    marker.setFlagType(null); // Valor por defecto si es inválido
                }
                markerManager.addMarker2(marker);
            }
        }
    }

    private Long getCurrentUserId() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return prefs.getLong("userId", -1); // Retorna -1 si no se encuentra el ID
    }


}


