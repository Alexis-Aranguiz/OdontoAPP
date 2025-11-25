package com.example.odontoapp.view.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.odontoapp.viewmodel.rememberProfileVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    val ctx = LocalContext.current
    val vm = rememberProfileVM(ctx)

    // Lanzador para elegir fotos de la galería
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            // Guardamos la URI como texto
            vm.onPhotoChange(uri.toString())
            // Importante: Persistir permisos de lectura para ver la foto en el futuro
            try {
                ctx.contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                // Algunos dispositivos no lo soportan, no es crítico para el demo
            }
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Mi perfil") }) }) { p ->
        if (vm.loading) {
            Box(Modifier.fillMaxSize().padding(p), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(p)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()) // Permite scroll si el teclado tapa
            ) {
                ElevatedCard(Modifier.fillMaxWidth().animateContentSize()) {
                    Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Foto de perfil", style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.height(8.dp))

                        // Mostrar foto si existe
                        if (vm.photoUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(vm.photoUri),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(120.dp) // Tamaño fijo circular o cuadrado
                                    .padding(4.dp),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text("Sin foto", color = MaterialTheme.colorScheme.outline)
                        }

                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(onClick = {
                            picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }) {
                            Text("Elegir desde galería")
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Campos de texto usando las nuevas funciones del VM
                ValidatedField(vm.name, vm::onNameChange, "Nombre", vm.nameErr)
                ValidatedField(vm.email, vm::onEmailChange, "Email", vm.emailErr)
                ValidatedField(vm.phone, vm::onPhoneChange, "Teléfono", vm.phoneErr)

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { vm.save() },
                    enabled = vm.canSave,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar Perfil")
                }
            }
        }
    }
}