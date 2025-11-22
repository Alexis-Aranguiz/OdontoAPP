package com.example.odontoapp.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.odontoapp.model.remote.ApiClients

/**
 * Tarjeta que muestra:
 * - Temperatura actual en Santiago (desde Open-Meteo)
 * - Hora local (desde WorldTimeAPI)
 */
@Composable
fun InfoCard() {
    var temp by remember { mutableStateOf("-- °C") }
    var localTime by remember { mutableStateOf("--:--") }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            loading = true

            // Coordenadas de Santiago de Chile
            val weatherResponse = ApiClients.weather.current(
                lat = -33.45,
                lon = -70.67
            )

            val timeResponse = ApiClients.time.now("America/Santiago")

            val t = weatherResponse.current_weather?.temperature
            temp = "${t?.toInt() ?: 0} °C"

            // datetime formato ISO: 2025-11-17T22:34:56.123456-03:00
            val dt = timeResponse.datetime
            localTime = dt?.substring(11, 16) ?: "--:--"

            error = null
        } catch (e: Exception) {
            error = "No se pudo obtener información"
        } finally {
            loading = false
        }
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Información actual",
                style = MaterialTheme.typography.titleMedium
            )

            if (loading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            } else if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {
                Text(
                    text = "Temperatura en Santiago: $temp",
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = "Hora local: $localTime",
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
