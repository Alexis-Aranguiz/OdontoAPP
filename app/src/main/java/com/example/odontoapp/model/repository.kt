package com.example.odontoapp.model

import android.content.Context
import com.example.odontoapp.model.remote.ApiClients
import com.example.odontoapp.view.notifyNow

interface ClinicRepository {
    suspend fun seedIfEmpty()
    suspend fun getDentists(): List<DentistEntity>
    suspend fun upsertMe(p: PatientEntity)
    suspend fun getMe(): PatientEntity?
    suspend fun saveAppointment(a: AppointmentEntity)
    suspend fun upcomingAppointments(): List<AppointmentEntity>
    suspend fun deleteAppointment(id: String)
    // 👇 NUEVO
    suspend fun updateAppointment(id: String, a: AppointmentEntity)
}

class ClinicRepositoryImpl private constructor(private val ctx: Context) : ClinicRepository {
    private val api = ApiClients.odontoApi

    override suspend fun seedIfEmpty() {}

    override suspend fun getDentists(): List<DentistEntity> = try {
        api.getDentists()
    } catch (e: Exception) { e.printStackTrace(); emptyList() }

    override suspend fun upsertMe(p: PatientEntity) {
        try { api.updatePatient(p) } catch (e: Exception) { e.printStackTrace() }
    }

    override suspend fun getMe(): PatientEntity? = try {
        api.getMe()
    } catch (e: Exception) { e.printStackTrace(); null }

    override suspend fun saveAppointment(a: AppointmentEntity) {
        try {
            api.createAppointment(a)
            ctx.notifyNow("Cita Agendada", "Tu cita ha sido registrada exitosamente.")
        } catch (e: Exception) { e.printStackTrace(); throw e }
    }

    override suspend fun upcomingAppointments(): List<AppointmentEntity> = try {
        val all = api.getAppointments()
        all.sortedBy { it.startsAtMillis }
    } catch (e: Exception) { e.printStackTrace(); emptyList() }

    override suspend fun deleteAppointment(id: String) {
        try {
            api.deleteAppointment(id)
            ctx.notifyNow("Cita Cancelada", "La cita ha sido eliminada.")
        } catch (e: Exception) { e.printStackTrace() }
    }

    // 👇 Implementación de Update
    override suspend fun updateAppointment(id: String, a: AppointmentEntity) {
        try {
            api.updateAppointment(id, a)
            ctx.notifyNow("Cita Modificada", "Se han actualizado los detalles.")
        } catch (e: Exception) { e.printStackTrace() }
    }

    companion object {
        @Volatile private var I: ClinicRepositoryImpl? = null
        fun get(ctx: Context) = I ?: synchronized(this) {
            I ?: ClinicRepositoryImpl(ctx.applicationContext).also { I = it }
        }
    }
}