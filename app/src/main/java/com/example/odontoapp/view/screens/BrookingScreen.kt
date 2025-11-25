package com.example.odontoapp.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.odontoapp.viewmodel.rememberBookingVM
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    dentistId: String,
    dentistName: String,
    onBooked: () -> Unit
) {
    val ctx = LocalContext.current
    val vm = rememberBookingVM(ctx, dentistId)
    val baseDate = LocalDate.now()
    val dateOptions = listOf(
        baseDate to "Hoy",
        baseDate.plusDays(1) to "Mañana",
        baseDate.plusDays(2) to "Pasado mañana"
    )

    var selectedDateIndex by remember { mutableStateOf(0) }
    var selectedHourIndex by remember { mutableStateOf<Int?>(null) }
    var bookingInProgress by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Sincronizar fecha inicial
    if (selectedDateIndex in dateOptions.indices) {
        vm.onDateChange(dateOptions[selectedDateIndex].first)
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Agendar cita") }) }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Profesional seleccionado:", style = MaterialTheme.typography.titleSmall)
            Text(dentistName, style = MaterialTheme.typography.titleMedium)

            // --- FECHAS ---
            Text("Selecciona una fecha", style = MaterialTheme.typography.titleSmall)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(dateOptions) { index, pair ->
                    FilterChip(
                        selected = selectedDateIndex == index,
                        onClick = {
                            selectedDateIndex = index
                            vm.onDateChange(pair.first)
                            selectedHourIndex = null
                            error = null
                        },
                        label = {
                            val formatter = DateTimeFormatter.ofPattern("dd/MM")
                            Text("${pair.second} (${pair.first.format(formatter)})")
                        }
                    )
                }
            }

            // --- HORAS ---
            Text("Selecciona un horario", style = MaterialTheme.typography.titleSmall)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(vm.slots) { index, time ->
                    FilterChip(
                        selected = selectedHourIndex == index,
                        onClick = {
                            selectedHourIndex = index
                            vm.onSlotSelected(time)
                            error = null
                        },
                        label = { Text(time.toString().substring(0, 5)) }
                    )
                }
            }

            if (error != null) {
                Text(text = error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(16.dp))

            // --- BOTÓN CONFIRMAR ---
            Button(
                onClick = {
                    if (selectedHourIndex == null) {
                        error = "Debes seleccionar una fecha y un horario."
                        return@Button
                    }
                    bookingInProgress = true
                    vm.book {
                        bookingInProgress = false
                        onBooked() // Vuelve atrás
                        // NOTA: La notificación se lanza automáticamente en el Repository
                    }
                },
                enabled = !bookingInProgress,
                modifier = Modifier.align(Alignment.End).fillMaxWidth()
            ) {
                if (bookingInProgress) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Confirmar cita")
                }
            }
        }
    }
}