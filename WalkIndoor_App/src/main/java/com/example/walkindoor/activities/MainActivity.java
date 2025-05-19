package com.example.walkindoor.activities;


import android.content.SharedPreferences;
import android.os.Bundle;


import android.content.Intent;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.walkindoor.R;
import com.example.walkindoor.activities.auxActivities.LoginActivity;
import com.example.walkindoor.activities.auxActivities.MapActivity;
import com.example.walkindoor.activities.auxActivities.SavedMapsActivity;

/**
 * Esta clase representa el punto de entrada de la aplicación.
 * Muestra la pantalla de inicio de la aplicación con opciones para crear un nuevo mapa,
 * buscar mapas guardados, acceder a configuraciones y mostrar ayuda.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Aquí se vinculan los botones con la interfaz XML
        Button btnCreateMap = findViewById(R.id.btnCreateMap); // El botón para crear un nuevo mapa.
        Button btnSearchMaps = findViewById(R.id.btnSearchMaps); // El botón para buscar un mapa guardado.

        // Manejo del botón "Crear Nuevo Mapa"
        btnCreateMap.setOnClickListener(view -> openActivity(MapActivity.class));

        // Manejo del botón "Buscar Mapas Guardados"
    //    btnSearchMaps.setOnClickListener(view -> openActivity(SavedMapsActivity.class));

        btnSearchMaps.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SavedMapsActivity.class);
            startActivity(intent);
        });
    }

    // verifica si el usuario ha iniciado sesión antes de abrir la app
    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }


    /**
     * Método auxiliar para abrir una actividad específica.
     *
     * @param activityClass Clase de la actividad que se desea abrir.
     */
    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(MainActivity.this, activityClass);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); // Agrega una animación de transición
    }

}
