package com.example.walkindoor.activities.auxActivities;


import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.walkindoor.R;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchDarkMode = findViewById(R.id.switchDarkMode);

        // Restaurar estado de modo oscuro
        boolean isDarkMode = getPreferences(MODE_PRIVATE).getBoolean("dark_mode", false);
        switchDarkMode.setChecked(isDarkMode);
        AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, isChecked ? "Modo oscuro activado" : "Modo oscuro desactivado", Toast.LENGTH_SHORT).show();

            // Guardar preferencia
            getPreferences(MODE_PRIVATE).edit().putBoolean("dark_mode", isChecked).apply();

            // Aplicar modo oscuro
            AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            recreate(); // Reiniciar actividad para aplicar cambios
        });
    }

}

