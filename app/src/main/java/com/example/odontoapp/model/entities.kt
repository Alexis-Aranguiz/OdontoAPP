package com.example.odontoapp.model

import androidx.room.*
import java.util.*

@Entity(tableName = "patients")
data class PatientEntity(
    @PrimaryKey val id: String = "me",   // paciente actual
    val name: String,
    val email: String?,
    val phone: String?,
    val photoUri: String?
)

@Entity(tableName = "dentists")
data class DentistEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val specialty: String
)

@Entity(
    tableName = "appointments",
    foreignKeys = [
        ForeignKey(entity = PatientEntity::class, parentColumns = ["id"], childColumns = ["patientId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = DentistEntity::class, parentColumns = ["id"], childColumns = ["dentistId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("patientId"), Index("dentistId")]
)
data class AppointmentEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val patientId: String = "me",
    val dentistId: String,
    val startsAtMillis: Long,
    val notes: String? = null
)

@Dao interface PatientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(p: PatientEntity)
    @Query("SELECT * FROM patients WHERE id = 'me'") suspend fun me(): PatientEntity?
}

@Dao interface DentistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertAll(list: List<DentistEntity>)
    @Query("SELECT * FROM dentists ORDER BY name") suspend fun all(): List<DentistEntity>
}

@Dao interface AppointmentDao {
    @Insert suspend fun insert(a: AppointmentEntity)
    @Query("SELECT * FROM appointments WHERE patientId = 'me' AND startsAtMillis >= :now ORDER BY startsAtMillis")
    suspend fun upcoming(now: Long): List<AppointmentEntity>
}

@Database(
    entities = [PatientEntity::class, DentistEntity::class, AppointmentEntity::class],
    version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun dentistDao(): DentistDao
    abstract fun appointmentDao(): AppointmentDao
}
