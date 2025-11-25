package com.example.odontoapp.viewmodel

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.odontoapp.model.AppointmentWithName
import com.example.odontoapp.model.ClinicRepository
import com.example.odontoapp.model.ClinicRepositoryImpl
import kotlinx.coroutines.launch

class AgendaViewModel(private val repo: ClinicRepository) : ViewModel() {

    var items by mutableStateOf<List<AppointmentWithName>>(emptyList())
        private set

    var loading by mutableStateOf(true)
        private set

    init {
        refresh()
    }

    fun refresh() = viewModelScope.launch {
        loading = true
        items = repo.upcomingAppointments()
        loading = false
    }
}

@Composable
fun rememberAgendaVM(ctx: Context) =
    remember { AgendaViewModel(ClinicRepositoryImpl.get(ctx)) }
