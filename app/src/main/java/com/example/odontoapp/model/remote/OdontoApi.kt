package com.example.odontoapp.model.remote

import com.example.odontoapp.model.AppointmentEntity
import com.example.odontoapp.model.DentistEntity
import com.example.odontoapp.model.PatientEntity
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface OdontoApi {

    // Obtener todos los dentistas
    @GET("dentists")
    suspend fun getDentists(): List<DentistEntity>

    // Obtener perfil del paciente actual
    // Asumimos que el backend maneja al usuario "me" o devuelve el primero por defecto para la demo
    @GET("patients/me")
    suspend fun getMe(): PatientEntity?

    // Crear/Actualizar paciente
    @POST("patients")
    suspend fun updatePatient(@Body patient: PatientEntity): PatientEntity

    // Obtener citas
    @GET("appointments")
    suspend fun getAppointments(): List<AppointmentEntity>

    // Crear cita
    @POST("appointments")
    suspend fun createAppointment(@Body appointment: AppointmentEntity): AppointmentEntity
}