package com.example.agrimexapp.datos

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


// Lo que enviamos al servidor
data class LoginRequest(val nombre: String, val credenciales: String)

// Lo que el servidor nos responde
data class LoginResponse(val id_usuario: Int, val rol_admin: Boolean, val id_departamento: Int?)

// 1. Mapeamos las rutas de tu API en Python
interface AgrimexApi {
    @GET("/api/equipos")
    suspend fun obtenerEquipos(): List<Equipo>
    // Nota: 'Equipo' es la tabla que creamos anteriormente

    // La etiqueta @Path inyecta el número del departamento en la URL
    @GET("/api/equipos/{idDepto}")
    suspend fun obtenerEquiposPorDepto(@Path("idDepto") idDepto: Int): List<Equipo>

    @POST("/api/login")
    suspend fun iniciarSesion(@Body request: LoginRequest): LoginResponse
}

// 2. Configuramos la conexión a tu computadora
object RetrofitClient {
    // ¡IMPORTANTE! Reemplaza esto con la IP de tu PC
    // Por ejemplo: "http://192.168.1.55:8000/"
    private const val BASE_URL = "http://192.168.4.238:8000/"

    val api: AgrimexApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Traduce las respuestas a Kotlin
            .build()
            .create(AgrimexApi::class.java)
    }
}

