package com.example.odontoapp.viewmodel

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.odontoapp.model.*
import kotlinx.coroutines.launch

class AgendaViewModel(private val repo: ClinicRepository): ViewModel() {
    var items by mutableStateOf<List<AppointmentEntity>>(emptyList()); private set
    var loading by mutableStateOf(true); private set
    init { refresh() }
    fun refresh() = viewModelScope.launch {
        loading = true
        items = repo.upcomingAppointments()
        loading = false
    }
}
@Composable fun rememberAgendaVM(ctx: Context) = remember { AgendaViewModel(ClinicRepositoryImpl.get(ctx)) }
