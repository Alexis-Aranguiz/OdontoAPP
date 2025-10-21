package com.example.odontoapp.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.odontoapp.view.notifyNow
import com.example.odontoapp.viewmodel.rememberBookingVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(dentistId: String, onBooked: () -> Unit) {
    val ctx = LocalContext.current
    val vm = rememberBookingVM(ctx, dentistId)

    Scaffold(topBar = { TopAppBar(title = { Text("Agendar cita") }) }) { p ->
        Column(Modifier.fillMaxSize().padding(p).padding(16.dp)) {
            Text("Selecciona fecha", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { vm.onDateChange(vm.date.minusDays(1)) }) { Text("← Ayer") }
                OutlinedButton(onClick = { vm.onDateChange(vm.date.plusDays(1)) }) { Text("Mañana →") }
            }
            Spacer(Modifier.height(16.dp))
            Text("Horarios disponibles", style = MaterialTheme.typography.titleMedium)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                vm.slots.forEach { t ->
                    AssistChip(onClick = { /* podrías marcar selección */ }, label = { Text(t.toString()) })
                }
            }
            Spacer(Modifier.height(24.dp))
            Button(onClick = {
                vm.book {
                    ctx.notifyNow("Cita agendada", "Te enviaremos un recordatorio.")
                    onBooked()
                }
            }, modifier = Modifier.fillMaxWidth()) { Text("Confirmar cita") }
        }
    }
}
