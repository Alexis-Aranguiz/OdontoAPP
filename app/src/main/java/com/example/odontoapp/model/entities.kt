package com.example.odontoapp.model

import com.squareup.moshi.JsonClass

// Eliminamos anotaciones @Entity de Room. Usamos data classes puras.


data class PatientEntity(
    val id: String = "me",
    val name: String,
    val email: String?,
    val phone: String?,
    // Nota: Para subir la foto al backend, idealmente se envía en Base64 o Multipart.
    // Aquí mantendremos el string para la URL o el Base64.
    val photoUri: String?
)


data class DentistEntity(
    val id: String,
    val name: String,
    val specialty: String
)


data class AppointmentEntity(
    val id: String? = null, // El ID lo suele generar el backend
    val patientId: String = "me",
    val dentistId: String?,
    val startsAtMillis: Long,
    val notes: String? = null
)
// Se eliminaron las interfaces DAO y la AppDatabase porque ya no usaremos Room.