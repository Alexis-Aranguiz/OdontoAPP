package com.example.odontoapp.model

object Validators {
    fun required(s: String) = if (s.isBlank()) "Campo requerido" else null
    fun email(s: String) = if (s.isNotBlank() && !Regex("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$").matches(s)) "Email inválido" else null
    fun phone(s: String) = if (s.isNotBlank() && s.length < 9) "Teléfono inválido" else null
}
