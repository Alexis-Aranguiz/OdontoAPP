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

// Estado interno de la tarjeta
private sealed interface InfoState {
    object Loading : InfoState
    data class Success(val temperature: Double, val time: String) : InfoState
    data class Error(val message: String) : InfoState
}

@Composable
fun InfoCard(
    modifier: Modifier = Modifier
) {
    var state: InfoState by remember { mutableStateOf(InfoState.Loading) }

    LaunchedEffect(Unit) {
        state = try {
            // Llamadas de red en hilo IO
            withContext(Dispatchers.IO) {
                // Clima en Santiago
                val weather = WeatherClient.api.getCurrentWeather(
                    lat = -33.45,   // Santiago
                    lon = -70.66
                )
                val temp = weather.currentWeather?.temperature
                    ?: error("Temperatura nula")

                // Hora local
                val timeResp = TimeClient.api.getTime("America/Santiago")
                val time = timeResp.datetime.substring(11, 16) // HH:MM

                InfoState.Success(temp, time)
            }
        } catch (e: Exception) {
            InfoState.Error("No se pudo obtener información")
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
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "Información actual",
                style = MaterialTheme.typography.titleMedium
            )

            when (val s = state) {
                InfoState.Loading -> {
                    Text(
                        text = "Cargando datos...",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    CircularProgressIndicator(
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                is InfoState.Success -> {
                    Text(
                        text = "Temperatura en Santiago: ${"%.1f".format(s.temperature)} °C",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = "Hora local: ${s.time}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                is InfoState.Error -> {
                    Text(
                        text = s.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
