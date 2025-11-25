package com.example.odontoapp.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.odontoapp.model.remote.TimeClient
import com.example.odontoapp.model.remote.WeatherClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// Estado interno de la tarjeta
private sealed interface InfoState {
    object Loading : InfoState
    data class Ready(val temperature: Double?, val time: String) : InfoState
}

/**
 * Tarjeta que muestra:
 * - Temperatura actual en Santiago (si la API de clima responde)
 * - Hora local (usa WorldTimeAPI; si falla, usa la hora del dispositivo)
 */
@Composable
fun InfoCard(
    modifier: Modifier = Modifier
) {
    var state: InfoState by remember { mutableStateOf(InfoState.Loading) }

    LaunchedEffect(Unit) {
        state = withContext(Dispatchers.IO) {
            // 1) Intentamos obtener la temperatura desde Open-Meteo
            val temp: Double? = try {
                val weatherResponse = WeatherClient.api.getCurrentWeather(
                    lat = -33.45,   // Santiago
                    lon = -70.67
                )
                weatherResponse.currentWeather?.temperature
            } catch (_: Exception) {
                null   // si falla, solo mostramos que no hay temperatura
            }

            // 2) Intentamos obtener la hora desde WorldTimeAPI; si falla, usamos hora local
            val timeText: String = try {
                val timeResponse = TimeClient.api.getTime("America/Santiago")
                val raw = timeResponse.datetime   // Ej: 2025-11-25T15:23:01.123Z
                if (raw.length >= 16) raw.substring(11, 16) else raw
            } catch (_: Exception) {
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                LocalTime.now(ZoneId.of("America/Santiago")).format(formatter)
            }

            InfoState.Ready(temp, timeText)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Información actual",
                style = MaterialTheme.typography.titleMedium
            )

            when (val s = state) {
                InfoState.Loading -> {
                    Text(
                        text = "Cargando datos de clima y hora…",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    CircularProgressIndicator(
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                is InfoState.Ready -> {
                    if (s.temperature != null) {
                        Text(
                            text = "Temperatura en Santiago: ${"%.1f".format(s.temperature)} °C",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    } else {
                        Text(
                            text = "Temperatura no disponible por el momento.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Text(
                        text = "Hora local: ${s.time}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
