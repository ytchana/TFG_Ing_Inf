package com.example.walkindoor.api;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Esta clase Retrofit será utilizada para enviar los datos a la REST API.
 * Esto permite que ApiService realice solicitudes HTTP al backend.
 */
public class ApiClient {
    private static final String BASE_URL = "http://192.168.1.50:8080/";
    private static Retrofit retrofit = null;
    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // Convertir JSON
                    .build();
        }

        return retrofit.create(ApiService.class);

    }

}

