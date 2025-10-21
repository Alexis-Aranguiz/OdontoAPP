package com.example.odontoapp.viewmodel

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.odontoapp.model.*
import kotlinx.coroutines.launch

class ExploreViewModel(private val repo: ClinicRepository): ViewModel() {
    var dentists by mutableStateOf<List<DentistEntity>>(emptyList()); private set
    init { viewModelScope.launch {
        repo.seedIfEmpty()
        dentists = repo.getDentists()
    } }
}
@Composable fun rememberExploreVM(ctx: Context) = remember { ExploreViewModel(ClinicRepositoryImpl.get(ctx)) }
