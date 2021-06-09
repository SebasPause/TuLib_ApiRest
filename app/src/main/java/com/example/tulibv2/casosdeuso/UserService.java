package com.example.tulibv2.casosdeuso;

import com.example.tulibv2.entities.GenerosLiterarios;
import com.example.tulibv2.entities.Libros;
import com.example.tulibv2.entities.Pagina;
import com.example.tulibv2.entities.Usuarios;
import com.example.tulibv2.entities.Valoraciones;
import com.example.tulibv2.userServices.BorrarValoracion;
import com.example.tulibv2.userServices.LoginRequest;
import com.example.tulibv2.userServices.LoginResponse;
import com.example.tulibv2.userServices.RegisterRequest;
import com.example.tulibv2.userServices.RegisterResponse;
import com.example.tulibv2.userServices.ValoracionRequest;
import com.example.tulibv2.userServices.ValoracionResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {

    @POST("ApplicationUser/Login")
    Call<LoginResponse> userLogin(@Body LoginRequest loginRequest);

    @POST("ApplicationUser/Registro")
    Call<RegisterResponse> userRegister(@Body RegisterRequest registerRequest);

    @GET("book/todos")
    Call<ArrayList<Libros>> getLibros(@Header("Authorization") String header);

    @GET("generoliterario")
    Call<ArrayList<GenerosLiterarios>> getGenerosLiterarios(@Header("Authorization") String header);

    @GET("valoraciones")
    Call<ArrayList<Valoraciones>> getValoraciones(@Header("Authorization") String header);

    @GET("contenido")
    Call<ArrayList<Pagina>> getPaginas(@Header("Authorization") String header);

    @GET("UserProfile")
    Call<Usuarios> getUsuario(@Header("Authorization") String header);

    @POST("valoraciones/insert")
    Call<ValoracionResponse> subirValoracion(@Body ValoracionRequest valoracion);

    @DELETE("valoraciones/deleteById")
    Call<ValoracionResponse> borrarValoracion(@Query("Id") String id, @Header("Authorization") String header);

    @PUT("UserProfile/update")
    Call<ValoracionResponse> subirFoto(@Body Usuarios usuario, @Header("Authorization") String header);

    @POST("book/insert")
    Call<ValoracionResponse> subirLibro(@Body Libros libro);

    @POST("generoliterario/insert")
    Call<ValoracionResponse> subirGenero(@Body GenerosLiterarios generoLiterario);

    @PUT("generoliterario/actualizarGenero")
    Call<ValoracionResponse> actualizarGenero(@Body GenerosLiterarios genero, @Header("Authorization") String header);

    @POST("contenido/insertPrimeraPagina")
    Call<ValoracionResponse> crearPrimeraPagina(@Body Pagina pagina);

    @PUT("contenido/actualizarPagina")
    Call<ValoracionResponse> actualizarPagina(@Body Pagina pagina, @Header("Authorization") String header);

    @PUT("book/actualizarInfo")
    Call<ValoracionResponse> actualizarInfoLibro(@Body Libros libro, @Header("Authorization") String header);

    @PUT("book/update")
    Call<ValoracionResponse> publicarLibro(@Body Libros libro, @Header("Authorization") String header);

    @DELETE("book/deleteById")
    Call<ValoracionResponse> borrarLibro(@Query("Id") String id, @Header("Authorization") String header);


}
