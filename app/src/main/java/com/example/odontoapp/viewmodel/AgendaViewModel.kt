package com.example.odontoapp.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.odontoapp.model.AppointmentEntity
import com.example.odontoapp.model.ClinicRepository
import com.example.odontoapp.model.ClinicRepositoryImpl
import kotlinx.coroutines.launch

data class AgendaUiItem(val appointment: AppointmentEntity, val dentistName: String)

class AgendaViewModel(private val repo: ClinicRepository) : ViewModel() {
    var items by mutableStateOf<List<AgendaUiItem>>(emptyList()); private set
    var loading by mutableStateOf(true); private set

    init { refresh() }

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

    // 👇 NUEVO: Función para modificar nota
    fun updateNote(appointment: AppointmentEntity, newNote: String) = viewModelScope.launch {
        loading = true
        // Creamos una copia de la cita con la nueva nota
        val updated = appointment.copy(notes = newNote)
        appointment.id?.let { id ->
            repo.updateAppointment(id, updated)
        }
        refresh()
    }
}

@Composable
fun rememberAgendaVM(ctx: Context) = remember { AgendaViewModel(ClinicRepositoryImpl.get(ctx)) }