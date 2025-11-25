package com.example.odontoapp.viewmodel

import android.content.Context
import com.example.odontoapp.MainDispatcherRule
import com.example.odontoapp.model.ClinicRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class BookingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<ClinicRepository>(relaxed = true)
    private val context = mockk<Context>(relaxed = true) // Mockeamos el contexto para que no falle

    @Test
    fun `al iniciar carga los horarios disponibles`() {
        val viewModel = BookingViewModel(repository, "dentista1", context)

        // Verificamos que la lista de slots no esté vacía al inicio
        assertTrue(viewModel.slots.isNotEmpty())
        assertEquals(5, viewModel.slots.size) // Según tu lógica eran 5 horarios fijos
    }

    @Test
    fun `al cambiar de fecha se resetea la hora seleccionada`() {
        val viewModel = BookingViewModel(repository, "dentista1", context)
        val horario = viewModel.slots[0]

        // 1. Seleccionamos una hora
        viewModel.onSlotSelected(horario)
        assertEquals(horario, viewModel.selectedSlot)

        // 2. Cambiamos la fecha
        viewModel.onDateChange(LocalDate.now().plusDays(1))

        // 3. La hora seleccionada debe volver a ser null
        assertNull(viewModel.selectedSlot)
    }

    @Test
    fun `book guarda la cita en el repositorio si todo esta ok`() = runTest {
        // GIVEN
        val viewModel = BookingViewModel(repository, "dentista1", context)
        val horario = viewModel.slots[0]
        viewModel.onSlotSelected(horario)

        // Simulamos que el guardado es exitoso (retorna Unit)
        coEvery { repository.saveAppointment(any()) } returns Unit

        var reservado = false

        // WHEN
        viewModel.book {
            reservado = true
        }

        // THEN
        // Verificamos que se llamó al repositorio
        coVerify { repository.saveAppointment(any()) }
        // Verificamos que se ejecutó el callback de éxito
        assertTrue(reservado)
    }
}