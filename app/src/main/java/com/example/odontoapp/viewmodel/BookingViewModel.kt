package com.example.odontoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.odontoapp.model.local.ClinicRepository
import com.example.odontoapp.model.remote.CitasClient
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class BookingViewModel(
    private val repo: ClinicRepository,
    private val dentistId: String
) : ViewModel() {

    var selectedDate: LocalDate? = null
    var selectedTime: LocalTime? = null
    var notes: String? = null

    /**
     * Guarda la cita en Room + la envía al backend
     */
    fun book(onDone: () -> Unit) {
        val date = selectedDate ?: return
        val time = selectedTime ?: return

        // Convertimos a millis
        val dateTime = date.atTime(time)
        val millis = dateTime
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val localAppointment = AppointmentEntity(
            id = null,
            dentistId = dentistId,
            patientId = "me",
            startsAtMillis = millis,
            notes = notes
        )

        viewModelScope.launch {

            // 1) Guardar localmente en Room
            repo.saveAppointment(localAppointment)

            // 2) Enviar al backend Spring Boot
            try {
                CitasClient.api.crearCita(
                    nombre = "Paciente",
                    dentistaId = dentistId,
                    fecha = date.toString(),
                    hora = time.toString(),
                    comentario = notes ?: ""
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            onDone()
        }
    }
}
