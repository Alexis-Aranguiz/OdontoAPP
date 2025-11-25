package com.example.odontoapp.model.remote

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiClients {
    // CAMBIAR ESTO POR TU URL DE RENDER (ej: https://mi-backend.onrender.com/api/)
    // Asegúrate de que termine en /
    private const val BACKEND_URL = "https://tu-app-en-render.com/api/"

    // Cliente para tu Backend Spring Boot
    val odontoApi: OdontoApi by lazy {
        Retrofit.Builder()
            .baseUrl(BACKEND_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(OdontoApi::class.java)
    }

    // Cliente para clima
    val weather: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/v1/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    // Cliente para hora
    val time: TimeApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://worldtimeapi.org/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(TimeApi::class.java)
    }
}