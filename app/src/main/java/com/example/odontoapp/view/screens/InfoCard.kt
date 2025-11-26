package com.example.odontoapp.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.odontoapp.model.remote.TimeClient
import com.example.odontoapp.model.remote.WeatherClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private sealed interface InfoState {
    object Loading : InfoState
    data class Ready(val temperature: Double?, val time: String) : InfoState
    data class Error(val message: String) : InfoState
}

@Composable
fun InfoCard(modifier: Modifier = Modifier) {
    var state: InfoState by remember { mutableStateOf(InfoState.Loading) }

    LaunchedEffect(Unit) {
        state = withContext(Dispatchers.IO) {
            try {
                // 1. Clima
                val apiKey = "a601ac205ebf3009c0ebd9e1349ea5ee"
                val weatherResponse = try {
                    WeatherClient.service.getCurrentWeather(
                        lat = -33.45,
                        lon = -70.67,
                        apiKey = apiKey
                    )
                } catch (e: Exception) {
                    null // Si falla el clima, seguimos con la hora
                }

                // 2. Hora
                val timeText: String = try {
                    val timeResponse = TimeClient.api.getTime("America/Santiago")
                    val raw = timeResponse.datetime // Ej: 2025-11-25T22:15:01.123Z

                    // 👇 AQUÍ ESTÁ EL CAMBIO:
                    // Antes era (11, 16) para HH:mm
                    // Ahora usamos (11, 19) para HH:mm:ss
                    if (raw.length >= 19) raw.substring(11, 19) else raw

                } catch (e: Exception) {
                    // Si falla la API, usamos la hora del celular con segundos
                    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                    LocalTime.now(ZoneId.of("America/Santiago")).format(formatter)
                }

                InfoState.Ready(weatherResponse?.main?.temp, timeText)

            } catch (e: Exception) {
                e.printStackTrace()
                InfoState.Error("Error de conexión")
            }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Información actual", style = MaterialTheme.typography.titleMedium)

            when (val s = state) {
                InfoState.Loading -> CircularProgressIndicator()

                is InfoState.Ready -> {
                    // Clima
                    if (s.temperature != null) {
                        Text("Temperatura: ${s.temperature.toInt()}°C")
                    } else {
                        Text("Temperatura: --")
                    }

                    // Hora (Ahora con segundos)
                    Text("Hora local: ${s.time}")
                }

                is InfoState.Error -> {
                    Text(text = s.message, color = Color.Red)
                }
            }
        }
    }
}