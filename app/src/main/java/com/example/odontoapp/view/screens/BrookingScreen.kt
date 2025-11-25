package com.example.odontoapp.view.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
    onBooked: () -> Unit,
    onBack: () -> Unit = onBooked   // si en Navigation solo pasas onBooked, sigue compilando
) {
    val ctx: Context = LocalContext.current
    val vm = rememberBookingVM(ctx, dentistId)

    val formatter = DateTimeFormatter.ofPattern("dd/MM")

    // Estado visual de selección (solo para la UI)
    var selectedDateIndex by remember { mutableIntStateOf(0) }
    var selectedSlotIndex by remember { mutableStateOf<Int?>(null) }

    val today = LocalDate.now()
    val dates = listOf(
        today,
        today.plusDays(1),
        today.plusDays(2)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agendar cita") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            // Profesional
            Text(
                text = "Profesional seleccionado:",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = dentistName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // -------- Fechas --------
            Text(
                text = "Selecciona una fecha",
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                dates.forEachIndexed { index, date ->
                    val label = when (index) {
                        0 -> "Hoy (${date.format(formatter)})"
                        1 -> "Mañana (${date.format(formatter)})"
                        else -> "Pasado mañana (${date.format(formatter)})"
                    }

                    val isSelected = selectedDateIndex == index

                    Button(
                        onClick = {
                            selectedDateIndex = index
                            vm.onDateChange(date)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isSelected)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(label)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // -------- Horarios --------
            Text(
                text = "Selecciona un horario",
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                vm.slots.forEachIndexed { index, time ->
                    val isSelected = selectedSlotIndex == index
                    Button(
                        onClick = {
                            selectedSlotIndex = index
                            vm.onSlotSelected(time)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isSelected)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(time.toString().substring(0, 5)) // "HH:mm"
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // -------- Botón confirmar --------
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            if (selectedSlotIndex != null) {

                                vm.book {
                                    // Volvemos atrás
                                    onBooked()

                                    // Lanzar notificación
                                    scheduleNotification(
                                        ctx = ctx,
                                        millis = vm.selectedDate!!.atTime(vm.selectedTime!!).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                                        dentistName = dentistName
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirmar cita")
                    }

                }
            }
        }
    }
}
