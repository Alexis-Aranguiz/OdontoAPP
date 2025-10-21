package com.example.odontoapp.view.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.odontoapp.viewmodel.rememberExploreVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(onReserve: (String) -> Unit) {
    val ctx = LocalContext.current
    val vm = rememberExploreVM(ctx)

    Scaffold(topBar = { TopAppBar(title = { Text("Explorar profesionales") }) }) { p ->
        val list = vm.dentists
        Crossfade(targetState = list.isEmpty(), label = "explore") { empty ->
            if (empty) Box(Modifier.fillMaxSize().padding(p), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            else LazyColumn(Modifier.fillMaxSize().padding(p).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(list) { d ->
                    ElevatedCard(Modifier.fillMaxWidth().animateContentSize()) {
                        Column(Modifier.padding(16.dp)) {
                            Text(d.name, style = MaterialTheme.typography.titleMedium)
                            Text(d.specialty, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { onReserve(d.id) }) { Text("Reservar") }
                        }
                    }
                }
            }
        }
    }
}
