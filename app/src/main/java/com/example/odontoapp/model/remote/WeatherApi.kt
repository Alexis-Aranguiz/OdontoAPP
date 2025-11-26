package com.example.odontoapp.model.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// --- 1. Modelos de datos ---

data class WeatherResponse(
    val main: MainInfo,
    val weather: List<WeatherDescription>,
    val name: String
)

data class MainInfo(
    val temp: Double,
    val humidity: Int
)

data class WeatherDescription(
    val description: String,
    val icon: String
)

// --- 2. Interfaz de la API ---

interface WeatherApi {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "es"
    ): WeatherResponse
}

// --- 3. Cliente Retrofit ---

object WeatherClient {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    // 👇 IMPORTANTE: Configuramos Moshi para que entienda Kotlin
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val service: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // 👇 Le pasamos nuestra instancia de moshi configurada
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(WeatherApi::class.java)
    }
}