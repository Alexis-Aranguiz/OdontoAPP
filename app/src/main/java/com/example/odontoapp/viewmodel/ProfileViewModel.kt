package com.example.odontoapp.viewmodel

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.odontoapp.model.*
import kotlinx.coroutines.launch

class ProfileViewModel(private val repo: ClinicRepository): ViewModel() {
    var me by mutableStateOf<PatientEntity?>(null); private set
    var name by mutableStateOf(""); var nameErr by mutableStateOf<String?>(null)
    var email by mutableStateOf(""); var emailErr by mutableStateOf<String?>(null)
    var phone by mutableStateOf(""); var phoneErr by mutableStateOf<String?>(null)
    var photoUri by mutableStateOf<String?>(null)

    init { viewModelScope.launch {
        repo.seedIfEmpty()
        me = repo.getMe()
        me?.let {
            name = it.name; email = it.email.orEmpty(); phone = it.phone.orEmpty(); photoUri = it.photoUri
        }
    } }

    fun onName(v: String){ name=v; nameErr = Validators.required(v) }
    fun onEmail(v: String){ email=v; emailErr = Validators.email(v) }
    fun onPhone(v: String){ phone=v; phoneErr = Validators.phone(v) }
    fun onPhoto(uri: String?){ photoUri = uri }

    val canSave get() = listOf(nameErr,emailErr,phoneErr).all{ it==null } && name.isNotBlank()
    fun save() = viewModelScope.launch {
        repo.upsertMe(PatientEntity(name = name, email = email.ifBlank{null}, phone = phone.ifBlank{null}, photoUri = photoUri))
        me = repo.getMe()
    }
}
@Composable fun rememberProfileVM(ctx: Context) = remember { ProfileViewModel(ClinicRepositoryImpl.get(ctx)) }
