package com.example.odontoapp.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    // aseguramos que el VM tenga la fecha inicial alineada
    if (selectedDateIndex in dateOptions.indices) {
        vm.onDateChange(dateOptions[selectedDateIndex].first)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agendar cita") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                "Profesional seleccionado:",
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                dentistName,
                style = MaterialTheme.typography.titleMedium
            )

            // --- FECHAS ---
            Text("Selecciona una fecha", style = MaterialTheme.typography.titleSmall)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(dateOptions) { index, pair ->
                    val selected = selectedDateIndex == index
                    FilterChip(
                        selected = selected,
                        onClick = {
                            selectedDateIndex = index
                            vm.onDateChange(pair.first)
                            selectedHourIndex = null
                            error = null
                        },
                        label = {
                            val label = pair.second
                            val formatter = DateTimeFormatter.ofPattern("dd/MM")
                            Text("$label (${pair.first.format(formatter)})")
                        }
                    )
                }
            }

            // --- HORAS ---
            Text("Selecciona un horario", style = MaterialTheme.typography.titleSmall)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(vm.slots) { index, time ->
                    val selected = selectedHourIndex == index
                    FilterChip(
                        selected = selected,
                        onClick = {
                            selectedHourIndex = index
                            vm.onSlotSelected(time)
                            error = null
                        },
                        label = {
                            Text(time.toString().substring(0,5)) // HH:MM
                        }
                    )
                }
            }

            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (selectedHourIndex == null) {
                        error = "Debes seleccionar una fecha y un horario."
                        return@Button
                    }
                    bookingInProgress = true
                    vm.book {
                        bookingInProgress = false
                        onBooked()
                    }
                },
                enabled = !bookingInProgress,
                modifier = Modifier.align(Alignment.End)
            ) {
                if (bookingInProgress) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .height(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Confirmar cita")
                }
            }
        }
    }
}
