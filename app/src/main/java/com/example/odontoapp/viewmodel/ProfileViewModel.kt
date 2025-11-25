package com.example.odontoapp.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.odontoapp.model.*
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repo: ClinicRepository,
    private val context: Context // Necesitamos el contexto para los mensajes
) : ViewModel() {

    var me by mutableStateOf<PatientEntity?>(null)
        private set

    // Campos del formulario
    var name by mutableStateOf("")
    var nameErr by mutableStateOf<String?>(null)

    var email by mutableStateOf("")
    var emailErr by mutableStateOf<String?>(null)

    var phone by mutableStateOf("")
    var phoneErr by mutableStateOf<String?>(null)

    var photoUri by mutableStateOf<String?>(null)

    // Estado de carga
    var loading by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            loading = true
            try {
                // Cargamos los datos actuales del servidor
                me = repo.getMe()
                me?.let {
                    name = it.name
                    email = it.email.orEmpty()
                    phone = it.phone.orEmpty()
                    photoUri = it.photoUri
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Si falla al cargar, no es crítico, el usuario puede llenarlo
            }
            loading = false
        }
    }

    fun onNameChange(v: String) {
        name = v
        nameErr = Validators.required(v)
    }

    fun onEmailChange(v: String) {
        email = v
        emailErr = Validators.email(v)
    }

    fun onPhoneChange(v: String) {
        phone = v
        phoneErr = Validators.phone(v)
    }

    fun onPhotoChange(uri: String?) {
        photoUri = uri
    }

    // Validación para habilitar el botón
    val canSave: Boolean
        get() = (nameErr == null && emailErr == null && phoneErr == null && name.isNotBlank())

    fun save() = viewModelScope.launch {
        loading = true
        try {
            val patient = PatientEntity(
                id = "me", // Enlazamos siempre al usuario actual
                name = name,
                email = email.ifBlank { null },
                phone = phone.ifBlank { null },
                photoUri = photoUri
            )

            // Enviamos al backend
            repo.upsertMe(patient)

            // Actualizamos la vista local
            me = repo.getMe()

            Toast.makeText(context, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
        }
        loading = false
    }
}

@Composable
fun rememberProfileVM(ctx: Context) = remember {
    ProfileViewModel(ClinicRepositoryImpl.get(ctx), ctx)
}