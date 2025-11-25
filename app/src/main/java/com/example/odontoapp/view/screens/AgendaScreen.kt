package com.example.odontoapp.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.odontoapp.viewmodel.rememberAgendaVM
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen() {
    val ctx = LocalContext.current
    val vm = rememberAgendaVM(ctx)

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis citas") }) }
    ) { p ->
        when {
            vm.loading -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(p),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            vm.items.isEmpty() -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(p),
                contentAlignment = Alignment.Center
            ) {
                Text("No tienes citas próximas")
            }

            else -> {
                val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(p),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(vm.items) { item ->
                        val appt = item.appointment
                        val dt = Instant.ofEpochMilli(appt.startsAtMillis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()

                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    text = "${dt.toLocalDate().format(dateFormatter)}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Hora: ${dt.toLocalTime().format(timeFormatter)}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Profesional: ${item.dentistName}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
