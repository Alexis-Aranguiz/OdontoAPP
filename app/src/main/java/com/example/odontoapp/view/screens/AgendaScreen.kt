package com.example.odontoapp.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.odontoapp.model.AppointmentEntity
import com.example.odontoapp.viewmodel.rememberAgendaVM
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen() {
    val ctx = LocalContext.current
    val vm = rememberAgendaVM(ctx)

    // Estado para el cuadro de diálogo de edición
    var showDialog by remember { mutableStateOf(false) }
    var currentEditingAppointment by remember { mutableStateOf<AppointmentEntity?>(null) }
    var newNoteText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { vm.refresh() }

    if (showDialog && currentEditingAppointment != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Modificar cita") },
            text = {
                Column {
                    Text("Agregar nota o comentario:")
                    OutlinedTextField(
                        value = newNoteText,
                        onValueChange = { newNoteText = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.updateNote(currentEditingAppointment!!, newNoteText)
                    showDialog = false
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Mis citas") }) }) { p ->
        when {
            vm.loading -> Box(Modifier.fillMaxSize().padding(p), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            vm.items.isEmpty() -> Box(Modifier.fillMaxSize().padding(p), contentAlignment = Alignment.Center) { Text("No tienes citas próximas") }
            else -> {
                val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

                LazyColumn(Modifier.fillMaxSize().padding(p), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(vm.items) { item ->
                        val appt = item.appointment
                        val dt = Instant.ofEpochMilli(appt.startsAtMillis).atZone(ZoneId.systemDefault()).toLocalDateTime()

                        ElevatedCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                            Column(Modifier.padding(16.dp)) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(Modifier.weight(1f)) {
                                        Text(dt.toLocalDate().format(dateFormatter), style = MaterialTheme.typography.titleMedium)
                                        Text("Hora: ${dt.toLocalTime().format(timeFormatter)}", style = MaterialTheme.typography.bodyMedium)
                                        Spacer(Modifier.height(4.dp))
                                        Text("Profesional: ${item.dentistName}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                                        if (!appt.notes.isNullOrBlank()) {
                                            Text("Nota: ${appt.notes}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                                        }
                                    }
                                    // Botones de Acción
                                    Row {
                                        // ✏️ Botón Editar
                                        IconButton(onClick = {
                                            currentEditingAppointment = appt
                                            newNoteText = appt.notes ?: ""
                                            showDialog = true
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                                        }
                                        // 🗑️ Botón Eliminar
                                        IconButton(onClick = {
                                            appt.id?.let { vm.cancelAppointment(it) }
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Cancelar", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}