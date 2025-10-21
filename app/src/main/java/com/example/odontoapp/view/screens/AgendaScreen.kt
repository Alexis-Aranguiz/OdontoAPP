package com.example.odontoapp.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.odontoapp.viewmodel.rememberAgendaVM
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen() {
    val ctx = LocalContext.current
    val vm = rememberAgendaVM(ctx)

    Scaffold(topBar = { TopAppBar(title = { Text("Mis citas") }) }) { p ->
        when {
            vm.loading -> Box(Modifier.fillMaxSize().padding(p), contentAlignment = Alignment.Center){ CircularProgressIndicator() }
            vm.items.isEmpty() -> Box(Modifier.fillMaxSize().padding(p), contentAlignment = Alignment.Center){ Text("No tienes citas próximas") }
            else -> LazyColumn(Modifier.fillMaxSize().padding(p)) {
                items(vm.items) { a ->
                    val date = Instant.ofEpochMilli(a.startsAtMillis).atZone(ZoneId.systemDefault()).toLocalDateTime()
                    ListItem(
                        headlineContent = { Text(date.toString()) },
                        supportingContent = { Text("Profesional: ${a.dentistId}") }
                    )
                    Divider()
                }
            }
        }
    }
}
