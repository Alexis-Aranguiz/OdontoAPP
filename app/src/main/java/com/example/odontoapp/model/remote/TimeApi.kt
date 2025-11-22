package com.example.odontoapp.model.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// ---------- Modelo de respuesta ----------

@JsonClass(generateAdapter = true)
data class TimeResponse(
    @Json(name = "datetime") val datetime: String
)

// ---------- Interfaz Retrofit ----------

interface TimeApi {

    // Ejemplo: GET https://worldtimeapi.org/api/timezone/America/Santiago
    @GET("api/timezone/{zone}")
    suspend fun getTime(@Path("zone") zone: String): TimeResponse
}

// ---------- Cliente singleton ----------

object TimeClient {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://worldtimeapi.org/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val api: TimeApi = retrofit.create(TimeApi::class.java)
}
