package com.example.odontoapp.model

import android.content.Context
import androidx.room.Room

interface ClinicRepository {
    suspend fun seedIfEmpty()
    suspend fun getDentists(): List<DentistEntity>
    suspend fun upsertMe(p: PatientEntity)
    suspend fun getMe(): PatientEntity?
    suspend fun saveAppointment(a: AppointmentEntity)
    suspend fun upcomingAppointments(): List<AppointmentEntity>
}

class ClinicRepositoryImpl private constructor(ctx: Context) : ClinicRepository {
    private val db = Room.databaseBuilder(ctx, AppDatabase::class.java, "clinic.db").build()

    override suspend fun seedIfEmpty() {
        if (db.dentistDao().all().isEmpty()) {
            db.dentistDao().insertAll(
                listOf(
                    DentistEntity(name = "Dra. Pérez", specialty = "Ortodoncia"),
                    DentistEntity(name = "Dr. Soto", specialty = "Endodoncia"),
                    DentistEntity(name = "Dra. Muñoz", specialty = "Implantología")
                )
            )
        }
        if (db.patientDao().me() == null) {
            db.patientDao().upsert(PatientEntity(name = "Paciente", email = null, phone = null, photoUri = null))
        }
    }

    override suspend fun getDentists() = db.dentistDao().all()
    override suspend fun upsertMe(p: PatientEntity) = db.patientDao().upsert(p)
    override suspend fun getMe() = db.patientDao().me()
    override suspend fun saveAppointment(a: AppointmentEntity) = db.appointmentDao().insert(a)
    override suspend fun upcomingAppointments(): List<AppointmentEntity> =
        db.appointmentDao().upcoming(System.currentTimeMillis())

    companion object {
        @Volatile private var I: ClinicRepositoryImpl? = null
        fun get(ctx: Context) = I ?: synchronized(this) {
            I ?: ClinicRepositoryImpl(ctx.applicationContext).also { I = it }
        }
    }
}
