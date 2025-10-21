package com.example.odontoapp.viewmodel

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.odontoapp.model.*
import kotlinx.coroutines.launch
import java.time.*

class BookingViewModel(private val repo: ClinicRepository, val dentistId: String): ViewModel() {
    var date by mutableStateOf(LocalDate.now().plusDays(1))
    var slots by mutableStateOf<List<LocalTime>>(emptyList())

    init { recomputeSlots() }

    fun onDateChange(d: LocalDate) { date = d; recomputeSlots() }

    private fun recomputeSlots() {
        // Slots 09:00–17:00 cada 30 min (demo)
        val list = mutableListOf<LocalTime>()
        var t = LocalTime.of(9,0)
        while (t.isBefore(LocalTime.of(17,30))) { list += t; t = t.plusMinutes(30) }
        slots = list
    }

    fun book(onBooked: () -> Unit) = viewModelScope.launch {
        val dt = date.atTime(slots.firstOrNull() ?: LocalTime.of(9,0))
        repo.saveAppointment(
            AppointmentEntity(
                dentistId = dentistId,
                startsAtMillis = dt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            )
        )
        onBooked()
    }
}
@Composable fun rememberBookingVM(ctx: Context, dentistId: String) =
    remember { BookingViewModel(ClinicRepositoryImpl.get(ctx), dentistId) }
