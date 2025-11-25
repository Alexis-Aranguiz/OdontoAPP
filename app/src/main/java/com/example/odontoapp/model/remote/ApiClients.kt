package com.example.odontoapp.model.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiClients {
    // Asegúrate que esta URL tenga la barra al final "/"
    private const val BACKEND_URL = "https://microsapp.onrender.com/"

    // 1. Creamos una instancia de Moshi que sepa leer Kotlin
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory()) // 👈 ESTO ES LO QUE FALTABA
        .build()

    // Cliente para tu Backend Spring Boot
    val odontoApi: OdontoApi by lazy {
        Retrofit.Builder()
            .baseUrl(BACKEND_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi)) // 👈 Pasamos el moshi configurado
            .build()
            .create(OdontoApi::class.java)
    }

    // Cliente para clima (Open-Meteo)
    val weather: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/v1/")
            .addConverterFactory(MoshiConverterFactory.create(moshi)) // También ayuda aquí
            .build()
            .create(WeatherApi::class.java)
    }

    // Cliente para hora (WorldTimeAPI)
    val time: TimeApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://worldtimeapi.org/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(TimeApi::class.java)
    }
}