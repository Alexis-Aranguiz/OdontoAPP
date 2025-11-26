package com.example.odontoapp.viewmodel

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.odontoapp.model.AppointmentEntity
import com.example.odontoapp.model.ClinicRepository
import com.example.odontoapp.model.ClinicRepositoryImpl
import com.example.odontoapp.model.remote.WeatherClient // Importante: Importamos el cliente del clima
import kotlinx.coroutines.launch

data class AgendaUiItem(val appointment: AppointmentEntity, val dentistName: String)

class AgendaViewModel(private val repo: ClinicRepository) : ViewModel() {

    // --- Estado de la Agenda ---
    var items by mutableStateOf<List<AgendaUiItem>>(emptyList()); private set
    var loading by mutableStateOf(true); private set

    // --- Estado del Clima (NUEVO) ---
    var weatherTemp by mutableStateOf("")         // Ej: "24°C"
    var weatherDesc by mutableStateOf("Cargando") // Ej: "Cielo claro"
    var weatherIconUrl by mutableStateOf("")      // URL del icono

    init {
        refresh()
        loadWeather() // Iniciamos la carga del clima al crear el ViewModel
    }

    // Lógica de Agenda (Citas)
    fun refresh() = viewModelScope.launch {
        loading = true
        try {
            val appointments = repo.upcomingAppointments()
            val dentists = repo.getDentists()
            items = appointments.map { appt ->
                val name = dentists.find { it.id == appt.dentistId }?.name ?: "Desconocido"
                AgendaUiItem(appt, name)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            items = emptyList()
        }
        loading = false
    }

    fun cancelAppointment(id: String) = viewModelScope.launch {
        loading = true
        repo.deleteAppointment(id)
        refresh()
    }

    fun updateNote(appointment: AppointmentEntity, newNote: String) = viewModelScope.launch {
        loading = true
        val updated = appointment.copy(notes = newNote)
        appointment.id?.let { id ->
            repo.updateAppointment(id, updated)
        }
        refresh()
    }

    // --- Lógica del Clima (NUEVO) ---
    fun loadWeather() = viewModelScope.launch {
        try {
            // Coordenadas (Santiago/San Bernardo aprox)
            val lat = -33.5922
            val lon = -70.6996
            val apiKey = "a601ac205ebf3009c0ebd9e1349ea5ee" // Tu API Key

            val response = WeatherClient.service.getCurrentWeather(lat, lon, apiKey)

            // Actualizamos las variables observables
            weatherTemp = "${response.main.temp.toInt()}°C"

            // Ponemos la primera letra en mayúscula
            weatherDesc = response.weather.firstOrNull()?.description
                ?.replaceFirstChar { it.uppercase() } ?: "Sin datos"

            // Obtenemos el icono
            val iconCode = response.weather.firstOrNull()?.icon ?: "01d"
            weatherIconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"

        } catch (e: Exception) {
            e.printStackTrace()
            weatherDesc = "Error clima"
            weatherTemp = "--"
        }
    }
}

@Composable
fun rememberAgendaVM(ctx: Context) = remember { AgendaViewModel(ClinicRepositoryImpl.get(ctx)) }