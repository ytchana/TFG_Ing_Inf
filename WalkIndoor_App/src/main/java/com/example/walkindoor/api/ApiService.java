package com.example.walkindoor.api;

import com.example.walkindoor.models.entities.MapFrontend;
import com.example.walkindoor.models.markers.Marker;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiService {

    // REGISTRO Y AUTENTICACIÓN DE USUARIO
    @POST("user/register")
    Call<User> registerUser(@Body User user);

    @GET("users/find")
    Call<UserDTO> findUser(@Query("identifier") String identifier);

    // CREACIÓN DE MAPA Y ASOCIACIÓN A USUARIO
    @POST("map")
    Call<MapFrontend> createMap(@Body MapFrontend map, @Query("userId") Long userId);

    // ENVÍO DE MARCADORES ASOCIADOS A UN MAPA
    @POST("marker")
    Call<Marker> addMarker(@Body Marker marker, @Query("mapId") Long mapId);

    // ACTUALIZA los marcadores
    @PUT("marker/{id}")
    Call<Marker> updateMarker(@Path("id") Long id, @Body Marker marker);

    // ELIMINA los marcadores
    @DELETE("marker/{id}")
    Call<Void> deleteMarker(@Path("id") Long id);

    // BUSCAR MAPAS
    @GET("maps/find")
    Call<MapFrontend> getMapByName(@Query("name") String mapName);
    @GET("maps/all")
    Call<List<MapFrontend>> getMaps();

    @POST("maps/create")
    Call<MapFrontend> createMap(@Body MapFrontend map);


}


