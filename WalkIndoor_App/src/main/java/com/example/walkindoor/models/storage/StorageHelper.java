package com.example.walkindoor.models.storage;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class StorageHelper {
    public static void saveToFile(Context context, String filename, String data) {
        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write((data + "\n").getBytes());
            fos.close();
            Log.d("Storage", "Datos guardados en archivo: " + filename);
        } catch (IOException e) {
            Log.e("Storage", "Error al guardar datos en archivo", e);
        }
    }

    public static String readFromFile(Context context, String filename) {
        try {
            FileInputStream fis = context.openFileInput(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            fis.close();
            return sb.toString();
        } catch (IOException e) {
            Log.e("Storage", "Error al leer archivo", e);
            return null;
        }
    }
}

