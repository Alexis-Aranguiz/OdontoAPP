package com.example.odontoapp.model

import android.content.Context
import com.example.odontoapp.model.remote.ApiClients
import com.example.odontoapp.view.notifyNow

interface ClinicRepository {
    suspend fun seedIfEmpty() // Ya no es necesario en remoto, pero lo mantenemos vacío por compatibilidad
    suspend fun getDentists(): List<DentistEntity>
    suspend fun upsertMe(p: PatientEntity)
    suspend fun getMe(): PatientEntity?
    suspend fun saveAppointment(a: AppointmentEntity)
    suspend fun upcomingAppointments(): List<AppointmentEntity>
}

class ClinicRepositoryImpl private constructor(private val ctx: Context) : ClinicRepository {

    // Ya no usamos 'db' (Room), usamos 'api'
    private val api = ApiClients.odontoApi

    override suspend fun seedIfEmpty() {
        // En una app real conectada a SpringBoot, el "seeding" se hace en el backend, no en la app móvil.
        // Dejamos esto vacío.
    }

    override suspend fun getDentists(): List<DentistEntity> {
        return try {
            api.getDentists()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun upsertMe(p: PatientEntity) {
        try {
            api.updatePatient(p)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun getMe(): PatientEntity? {
        return try {
            api.getMe()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun saveAppointment(a: AppointmentEntity) {
        try {
            api.createAppointment(a)
            // Requisito de notificación al agendar
            ctx.notifyNow("Cita Agendada", "Tu cita ha sido registrada exitosamente.")
        } catch (e: Exception) {
            e.printStackTrace()
            throw e // Lanzamos error para que la UI lo sepa
        }
    }

    override suspend fun upcomingAppointments(): List<AppointmentEntity> {
        return try {
            val all = api.getAppointments()
            val now = System.currentTimeMillis()
            // Filtramos en el cliente (aunque idealmente el backend debería tener un endpoint para esto)
            all.filter { it.startsAtMillis >= now }
                .sortedBy { it.startsAtMillis }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    companion object {
        @Volatile
        private var I: ClinicRepositoryImpl? = null

        fun get(ctx: Context) = I ?: synchronized(this) {
            I ?: ClinicRepositoryImpl(ctx.applicationContext).also { I = it }
        }
    }
}