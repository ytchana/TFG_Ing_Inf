package com.example.walkindoor.activities.auxActivities;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.walkindoor.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador personalizado para mostrar una lista de mapas guardados en 'SavedMapsActivity'.
 */

/*
public class SavedMapsAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final List<String> originalData; // Lista original sin filtrar
    private final List<String> filteredData; // Lista filtrada de mapas guardados

    public SavedMapsAdapter(Context context, List<String> maps) {
        super(context, R.layout.item_saved_map, maps);
        this.context = context;
        this.originalData = new ArrayList<>(maps);
        this.filteredData = new ArrayList<>(maps);;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Verificar que la lista no esté vacía antes de acceder a elementos
        if (filteredData.isEmpty() || position >= filteredData.size()) {
            Log.e("SavedMapsAdapter", "Error: Índice fuera de los límites. Posición: " + position + ", Tamaño: " + filteredData.size());
            return new View(context); // Evita la excepción devolviendo una vista vacía
        }

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_saved_map, parent, false);
        }

        // Configurar el texto del mapa guardado
        TextView textViewMap = convertView.findViewById(R.id.textViewMap);
        textViewMap.setText(filteredData.get(position));

        return convertView;
    }


    */
/**
     * Filtra los mapas según la consulta ingresada por el usuario.
     *
     * @param query Texto de búsqueda.
     *//*

    public void filter(String query) {
        query = query.toLowerCase();
        List<String> filteredList = new ArrayList<>();

        for (String mapName : originalData) {
            if (mapName.toLowerCase().contains(query)) {
                filteredList.add(mapName);
            }
        }
        Log.d("SavedMapsAdapter", "Mapas filtrados: " + filteredList.size()); // Verifica cuántos resultados se encontraron

        clear();
        addAll(filteredList);
        notifyDataSetChanged();
    }

}

*/



import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.example.walkindoor.R;
import com.example.walkindoor.models.entities.MapFrontend;


import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para gestionar la lista de mapas guardados y aplicar filtros de búsqueda.
 */
public class SavedMapsAdapter extends ArrayAdapter<String> implements Filterable {
    private Context context;
    private List<String> originalList;
    private List<String> filteredList;

    public SavedMapsAdapter(Context context, List<String> mapNames) {
        super(context, R.layout.item_saved_map, mapNames);
        this.context = context;
        this.originalList = new ArrayList<>(mapNames); // Guarda la lista original para filtrar
        this.filteredList = new ArrayList<>(mapNames);
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public String getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_saved_map, parent, false);
            holder = new ViewHolder();
            holder.mapNameTextView = convertView.findViewById(R.id.textViewMap);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String mapName = getItem(position);
        holder.mapNameTextView.setText(mapName);

        // Manejar clic en un mapa para abrirlo
        convertView.setOnClickListener(view -> openMap(mapName));

        return convertView;
    }

    /**
     * Método para abrir un mapa seleccionado.
     */
    private void openMap(String mapName) {
        Log.d("SavedMapsAdapter", "Abriendo mapa: " + mapName);

        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtra("mapName", mapName);
        context.startActivity(intent);
    }

    /**
     * Filtrar la lista de mapas según el texto ingresado en `SearchView`.
     */
    public void filter(String query) {
        query = query.toLowerCase();
        filteredList.clear();

        for (String mapName : originalList) {
            if (mapName.toLowerCase().contains(query)) {
                filteredList.add(mapName);
            }
        }

        Log.d("SavedMapsAdapter", "Mapas filtrados: " + filteredList.size());
        notifyDataSetChanged();
    }

    /**
     * ViewHolder para mejorar rendimiento de `ListView`.
     */
    private static class ViewHolder {
        TextView mapNameTextView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<String> filteredData = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredData.addAll(originalList);
                } else {
                    String query = constraint.toString().toLowerCase();
                    for (String mapName : originalList) {
                        Log.d("SavedMapsAdapter", "Mapa cargado: " + mapName);
                        if (mapName.toLowerCase().contains(query)) {
                            filteredData.add(mapName);
                        }
                    }
                }

                results.values = filteredData;
                results.count = filteredData.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList.clear();
                filteredList.addAll((List<String>) results.values);
                notifyDataSetChanged();
            }
        };
    }
}
