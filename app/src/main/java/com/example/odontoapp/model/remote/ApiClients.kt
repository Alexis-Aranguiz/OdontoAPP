package com.example.odontoapp.model.remote

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Clientes Retrofit para las APIs externas:
 * - Clima (Open-Meteo)
 * - Hora (WorldTimeAPI)
 */
object ApiClients {

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

