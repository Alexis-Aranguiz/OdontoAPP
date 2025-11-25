package com.example.odontoapp.model.remote

import com.example.odontoapp.model.AppointmentEntity
import com.example.odontoapp.model.DentistEntity
import com.example.odontoapp.model.PatientEntity
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface OdontoApi {
    @GET("dentists")
    suspend fun getDentists(): List<DentistEntity>

    @GET("patients/me")
    suspend fun getMe(): PatientEntity?

    @POST("patients")
    suspend fun updatePatient(@Body patient: PatientEntity): PatientEntity

    @GET("appointments")
    suspend fun getAppointments(): List<AppointmentEntity>

    @POST("appointments")
    suspend fun createAppointment(@Body appointment: AppointmentEntity): AppointmentEntity

    @DELETE("appointments/{id}")
    suspend fun deleteAppointment(@Path("id") id: String)

    // 👇 NUEVO: Actualizar Cita
    @PUT("appointments/{id}")
    suspend fun updateAppointment(@Path("id") id: String, @Body appointment: AppointmentEntity): AppointmentEntity
}