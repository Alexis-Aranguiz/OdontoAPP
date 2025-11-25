package com.example.odontoapp.viewmodel

import com.example.odontoapp.MainDispatcherRule
import com.example.odontoapp.model.AppointmentEntity
import com.example.odontoapp.model.ClinicRepository
import com.example.odontoapp.model.DentistEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AgendaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Mock del repositorio (simulamos su comportamiento)
    private val repository = mockk<ClinicRepository>(relaxed = true)

    // Datos de prueba falsos
    private val dentistaPrueba = DentistEntity(id = "d01", name = "Juan Pérez", specialty = "Ortodoncia")
    private val citaPrueba = AppointmentEntity(
        id = "c01",
        patientId = "me",
        dentistId = "d01", // Coincide con el dentista
        startsAtMillis = 1700000000000L,
        notes = "Nota test"
    )

    @Test
    fun `refresh carga citas y asigna nombres de dentistas correctamente`() = runTest {
        // 1. GIVEN (Dado que el repo devuelve estos datos)
        coEvery { repository.upcomingAppointments() } returns listOf(citaPrueba)
        coEvery { repository.getDentists() } returns listOf(dentistaPrueba)

        // 2. WHEN (Cuando inicializamos el ViewModel)
        val viewModel = AgendaViewModel(repository)
        // (El init llama a refresh automáticamente, pero esperamos a que termine con runTest)

        // 3. THEN (Entonces la lista debe tener 1 item y el nombre correcto)
        assertEquals(1, viewModel.items.size)
        assertEquals("Juan Pérez", viewModel.items[0].dentistName)
        assertFalse(viewModel.loading)
    }

    @Test
    fun `refresh maneja errores y deja la lista vacia`() = runTest {
        // 1. GIVEN (Dado que el repo falla)
        coEvery { repository.upcomingAppointments() } throws RuntimeException("Error de red")

        // 2. WHEN (Inicializamos)
        val viewModel = AgendaViewModel(repository)

        // 3. THEN (La lista debe estar vacía y no debe crashear)
        assertTrue(viewModel.items.isEmpty())
        assertFalse(viewModel.loading)
    }

    @Test
    fun `cancelAppointment llama al repositorio para borrar`() = runTest {
        // 1. GIVEN
        val viewModel = AgendaViewModel(repository)

        // 2. WHEN (Llamamos a cancelar)
        viewModel.cancelAppointment("c01")

        // 3. THEN (Verificamos que se llamó a deleteAppointment en el repo)
        coVerify { repository.deleteAppointment("c01") }
    }
}