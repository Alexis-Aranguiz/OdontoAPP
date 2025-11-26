package com.example.odontoapp.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.odontoapp.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.odontoapp.model.ClinicRepositoryImpl
class ProfileViewModel(
    private val repo: ClinicRepository,
    private val context: Context
) : ViewModel() {

    var me by mutableStateOf<PatientEntity?>(null); private set

    // Campos
    var name by mutableStateOf("")
    var nameErr by mutableStateOf<String?>(null)
    var email by mutableStateOf("")
    var emailErr by mutableStateOf<String?>(null)
    var phone by mutableStateOf("")
    var phoneErr by mutableStateOf<String?>(null)
    var photoUri by mutableStateOf<String?>(null)

    // Estados de UI
    var loading by mutableStateOf(false); private set

    // 👇 NUEVO: Estado para mostrar la animación
    var showAnimation by mutableStateOf(false); private set

    init {
        viewModelScope.launch {
            loading = true
            try {
                me = repo.getMe()
                me?.let {
                    name = it.name
                    email = it.email.orEmpty()
                    phone = it.phone.orEmpty()
                    photoUri = it.photoUri
                }
            } catch (e: Exception) { e.printStackTrace() }
            loading = false
        }
    }

    fun onNameChange(v: String) { name = v; nameErr = Validators.required(v) }
    fun onEmailChange(v: String) { email = v; emailErr = Validators.email(v) }
    fun onPhoneChange(v: String) { phone = v; phoneErr = Validators.phone(v) }
    fun onPhotoChange(uri: String?) { photoUri = uri }

    val canSave: Boolean get() = (nameErr == null && emailErr == null && phoneErr == null && name.isNotBlank())

    fun save() = viewModelScope.launch {
        loading = true
        // Ocultamos animación previa si hubiera
        showAnimation = false

        try {
            val patient = PatientEntity("me", name, email.ifBlank { null }, phone.ifBlank { null }, photoUri)
            repo.upsertMe(patient)
            me = repo.getMe()

            // 👇 ÉXITO: Quitamos carga y mostramos animación
            loading = false
            showAnimation = true

            // Mantenemos la animación visible por 3 segundos y luego la quitamos
            delay(3000)
            showAnimation = false

        } catch (e: Exception) {
            loading = false
            e.printStackTrace()
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }

    }

}
// Fuera de la clase (al final del archivo)
@Composable
fun rememberProfileVM(ctx: Context) = remember {
    ProfileViewModel(ClinicRepositoryImpl.get(ctx), ctx)
}