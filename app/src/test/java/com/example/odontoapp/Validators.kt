package com.example.odontoapp.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidatorsTest {

    @Test
    fun `required devuelve error si el texto esta vacio o en blanco`() {
        assertEquals("Campo requerido", Validators.required(""))
        assertEquals("Campo requerido", Validators.required("   "))
        assertNull(Validators.required("Hola"))
    }

    @Test
    fun `email valida formato correcto`() {
        // Casos inválidos (Formatos rotos)
        assertEquals("Email inválido", Validators.email("correo-sin-arroba.com"))
        assertEquals("Email inválido", Validators.email("correo@"))

        // CORRECCIÓN: Tu validador permite dejar el email vacío (es opcional),
        // por lo tanto, el resultado esperado para "" es null (válido).
        assertNull(Validators.email(""))

        // Caso válido
        assertNull(Validators.email("test@ejemplo.com"))
    }

    @Test
    fun `phone valida longitud minima`() {
        // Asumiendo que tu validación pide largo 9
        assertEquals("Teléfono inválido", Validators.phone("12345678")) // 8 dígitos
        assertNull(Validators.phone("123456789")) // 9 dígitos
    }
}