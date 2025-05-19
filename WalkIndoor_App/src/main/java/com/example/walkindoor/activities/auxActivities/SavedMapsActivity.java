package com.example.walkindoor.activities.auxActivities;
/*


import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.walkindoor.R;
import com.example.walkindoor.api.ApiClient;
import com.example.walkindoor.api.ApiService;
import com.example.walkindoor.models.entities.MapFrontend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SavedMapsActivity extends AppCompatActivity {
    private SavedMapsAdapter adapter;
    private ListView listViewMaps;
    private List<MapFrontend> mapFrontendList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_maps);

        ListView listView = findViewById(R.id.listViewMaps);
        if (listViewMaps == null) {
            Log.e("SavedMapsActivity", "Error: listViewMaps es nulo. Verifica el ID en activity_saved_maps.xml.");
            return;
        }

        SearchView searchView = findViewById(R.id.searchViewMaps);

        // Inicializar el adaptador con los datos
        adapter = new SavedMapsAdapter(this, new ArrayList<>());
        listViewMaps.setAdapter(adapter);
//        listView.setAdapter(adapter);

        // Llamar a la API para obtener los mapas guardados
        loadSavedMaps();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });
    }

    private void loadSavedMaps() {
        ApiService apiService = ApiClient.getApiService();
        Call<List<MapFrontend>> call = apiService.getMaps();

        call.enqueue(new Callback<List<MapFrontend>>() {
            @Override
            public void onResponse(Call<List<MapFrontend>> call, Response<List<MapFrontend>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mapFrontendList = response.body();
                    List<String> mapNames = new ArrayList<>();

                    for (MapFrontend map : mapFrontendList) {
                        mapNames.add(map.getName());
                    }

                    adapter.clear();
                    adapter.addAll(mapNames);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(SavedMapsActivity.this, "No hay mapas en el servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MapFrontend>> call, Throwable t) {
                Toast.makeText(SavedMapsActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private Map<String, ?> getSavedMaps() {
        SharedPreferences prefs = getSharedPreferences("SavedMaps", MODE_PRIVATE);
        Map<String, ?> savedMaps = prefs.getAll();

        Log.d("SavedMapsActivity", "Mapas encontrados en SharedPreferences: " + savedMaps.keySet());
        return savedMaps;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSavedMaps(); // Recargar los mapas guardados cada vez que se abra la actividad
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("SavedMapsActivity", "La actividad se está cerrando.");
    }


}
*/


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.walkindoor.R;
import com.example.walkindoor.api.ApiClient;
import com.example.walkindoor.api.ApiService;
import com.example.walkindoor.models.entities.MapFrontend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SavedMapsActivity extends AppCompatActivity {
    private SavedMapsAdapter adapter;
    private List<MapFrontend> mapFrontendList = new ArrayList<>();
    private ListView listViewMaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_maps);

        listViewMaps = findViewById(R.id.listViewMaps);
        SearchView searchView = findViewById(R.id.searchViewMaps);

        if (listViewMaps == null) {
            Log.e("SavedMapsActivity", "Error: listViewMaps es nulo.");
            return;
        }

        adapter = new SavedMapsAdapter(this, new ArrayList<>());
        listViewMaps.setAdapter(adapter);

        // Cargar mapas guardados desde el backend
        loadSavedMaps();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("SavedMapsActivity", "Buscando mapa: " + query);
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });

        listViewMaps.setOnItemClickListener((parent, view, position, id) -> {
            String selectedMapName = (String) parent.getItemAtPosition(position);
            fetchMapDetails(selectedMapName);
        });
    }

    /**
     * Carga la lista de mapas guardados desde el backend.
     */
    private void loadSavedMaps() {
        ApiService apiService = ApiClient.getApiService();
        Call<List<MapFrontend>> call = apiService.getMaps();

        call.enqueue(new Callback<List<MapFrontend>>() {
            @Override
            public void onResponse(Call<List<MapFrontend>> call, Response<List<MapFrontend>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MapFrontend> mapFrontendList = response.body();
                    List<String> mapNames = new ArrayList<>();

                    for (MapFrontend mapFrontend : mapFrontendList) {
                        mapNames.add(mapFrontend.getName());
                        Log.d("SavedMapsActivity", "Mapa cargado: " + mapFrontend.getName());
                    }

                    adapter = new SavedMapsAdapter(SavedMapsActivity.this, mapNames);
                    listViewMaps.setAdapter(adapter);
                } else {
                    Log.w("SavedMapsActivity", "No se encontraron mapas.");
                }
            }

            @Override
            public void onFailure(Call<List<MapFrontend>> call, Throwable t) {
                Toast.makeText(SavedMapsActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Obtiene los detalles de un mapa específico y lo abre en `MapActivity`.
     */
    private void fetchMapDetails(String mapName) {
        ApiService apiService = ApiClient.getApiService();
        Call<MapFrontend> call = apiService.getMapByName(mapName);

        call.enqueue(new Callback<MapFrontend>() {
            @Override
            public void onResponse(Call<MapFrontend> call, Response<MapFrontend> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MapFrontend map = response.body();
                    Intent intent = new Intent(SavedMapsActivity.this, MapActivity.class);
                    intent.putExtra("mapData", (Serializable) map);
                    startActivity(intent);
                } else {
                    Toast.makeText(SavedMapsActivity.this, "Mapa no encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MapFrontend> call, Throwable t) {
                Toast.makeText(SavedMapsActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

