package com.example.odontoapp.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
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
    val dentistId: String
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
        // mismos horarios que tu UI: 09:00, 10:00, 11:30, 15:00, 16:30
        slots = listOf(
            LocalTime.of(9, 0),
            LocalTime.of(10, 0),
            LocalTime.of(11, 30),
            LocalTime.of(15, 0),
            LocalTime.of(16, 30)
        )
    }

    fun onSlotSelected(t: LocalTime) {
        selectedSlot = t
    }

    fun book(onBooked: () -> Unit) = viewModelScope.launch {
        val chosen = selectedSlot ?: slots.firstOrNull() ?: return@launch
        val dt = date.atTime(chosen)

        repo.saveAppointment(
            AppointmentEntity(
                dentistId = dentistId,
                startsAtMillis = dt.atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            )
        )
        // aquí dentro normalmente tu repo disparaba la notificación
        onBooked()
    }
}

@Composable
fun rememberBookingVM(ctx: Context, dentistId: String) =
    androidx.compose.runtime.remember {
        BookingViewModel(ClinicRepositoryImpl.get(ctx), dentistId)
    }
