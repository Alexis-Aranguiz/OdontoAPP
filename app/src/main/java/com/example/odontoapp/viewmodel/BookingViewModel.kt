package com.example.odontoapp.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.odontoapp.model.AppointmentEntity
import com.example.odontoapp.model.ClinicRepository
import com.example.odontoapp.model.ClinicRepositoryImpl
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class BookingViewModel(
    private val repo: ClinicRepository,
    val dentistId: String,
    private val context: Context // Necesitamos el contexto para mostrar Toasts de error
) : ViewModel() {

    var date by mutableStateOf(LocalDate.now())
        private set
    var slots by mutableStateOf<List<LocalTime>>(emptyList())
        private set
    var selectedSlot by mutableStateOf<LocalTime?>(null)
        private set

    init {
        recomputeSlots()
    }

    fun onDateChange(d: LocalDate) {
        date = d
        recomputeSlots()
        selectedSlot = null
    }

    private fun recomputeSlots() {
        // Horarios fijos de ejemplo
        slots = listOf(
            LocalTime.of(9, 0), LocalTime.of(10, 0),
            LocalTime.of(11, 30), LocalTime.of(15, 0), LocalTime.of(16, 30)
        )
    }

    fun onSlotSelected(t: LocalTime) {
        selectedSlot = t
    }

    fun book(onBooked: () -> Unit) = viewModelScope.launch {
        val chosen = selectedSlot ?: return@launch
        val dt = date.atTime(chosen)

        val newAppointment = AppointmentEntity(
            dentistId = dentistId,
            startsAtMillis = dt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            // patientId se envía como "me" por defecto desde la Entity
        )

        try {
            // Intentamos guardar en el backend
            repo.saveAppointment(newAppointment)
            // Si funciona, ejecutamos la acción de éxito (volver atrás)
            onBooked()
        } catch (e: Exception) {
            // SI FALLA: Atrapamos el error para que la app NO se cierre
            e.printStackTrace()
            Log.e("BOOKING_ERROR", "Error al reservar: ${e.message}")
            Toast.makeText(context, "Error al reservar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
fun rememberBookingVM(ctx: Context, dentistId: String) = remember {
    BookingViewModel(ClinicRepositoryImpl.get(ctx), dentistId, ctx)
}