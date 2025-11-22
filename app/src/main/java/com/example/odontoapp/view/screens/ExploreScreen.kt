package com.example.odontoapp.view.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.odontoapp.viewmodel.rememberExploreVM

@Composable
fun ExploreScreen(
    onReserve: (dentistId: String, dentistName: String) -> Unit
) {
    val ctx = LocalContext.current
    val vm = rememberExploreVM(ctx)

    Scaffold(
        topBar = { TopAppBar(title = { Text("Explorar profesionales") }) }
    ) { paddingValues ->
        val list = vm.dentists

        Crossfade(targetState = list.isEmpty(), label = "explore") { empty ->
            if (empty) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Cargando profesionales…")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Tarjeta de clima + hora
                    item {
                        InfoCard()
                    }

                    // Lista de dentistas
                    items(list) { d ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .animateContentSize()
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    text = d.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = d.specialty,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Button(
                                    onClick = { onReserve(d.id, d.name) },
                                    modifier = Modifier
                                        .padding(top = 8.dp)
                                ) {
                                    Text("Reservar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
