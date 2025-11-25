package com.example.odontoapp.model.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

data class CitaRequest(
    val pacienteNombre: String,
    val dentistaId: String,
    val fecha: String,
    val hora: String,
    val comentario: String
)

interface CitasApi {

    @POST("/citas")
    suspend fun crearCita(@Body body: CitaRequest): CitaRequest
}

object CitasClient {

    val api: CitasApi by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080") // EMULADOR
            .addConverterFactory(retrofit2.converter.moshi.MoshiConverterFactory.create())
            .build()
            .create(CitasApi::class.java)
    }
}

// Función auxiliar para el VM:
suspend fun CitasApi.crearCita(nombre: String, dentistaId: String, fecha: String, hora: String, comentario: String) =
    crearCita(
        CitaRequest(
            pacienteNombre = nombre,
            dentistaId = dentistaId,
            fecha = fecha,
            hora = hora,
            comentario = comentario
        )
    )
