package com.example.odontoapp.view.screens

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

// 👇 IMPORTS PARA LOTTIE Y DIAGNÓSTICO
import com.airbnb.lottie.compose.*
import com.example.odontoapp.R
import com.example.odontoapp.viewmodel.rememberProfileVM
import androidx.compose.ui.graphics.Color // Para el color rojo/amarillo
import androidx.compose.foundation.background // Para pintar el fondo
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    val ctx = LocalContext.current
    val vm = rememberProfileVM(ctx)

    // Configuración de la foto (Galería)
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            vm.onPhotoChange(uri.toString())
            try {
                ctx.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) { }
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
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- TARJETA DE FOTO ---
                ElevatedCard(Modifier.fillMaxWidth().animateContentSize()) {
                    Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Foto de perfil", style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.height(8.dp))

                        if (vm.photoUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(vm.photoUri),
                                contentDescription = null,
                                modifier = Modifier.size(120.dp).padding(4.dp),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text("Sin foto", color = MaterialTheme.colorScheme.outline)
                        }

                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(onClick = { picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                            Text("Elegir desde galería")
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // --- CAMPOS DE TEXTO ---
                ValidatedField(vm.name, vm::onNameChange, "Nombre", vm.nameErr)
                ValidatedField(vm.email, vm::onEmailChange, "Email", vm.emailErr)
                ValidatedField(vm.phone, vm::onPhoneChange, "Teléfono", vm.phoneErr)

                Spacer(Modifier.height(24.dp))

                // --- BOTÓN GUARDAR ---
                Button(
                    onClick = { vm.save() },
                    // En modo diagnóstico habilitamos siempre el botón
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar Perfil")
                }

                Spacer(Modifier.height(24.dp))

                // ============================================================
                // 👇 ZONA DE DIAGNÓSTICO (FORZAMOS LA ANIMACIÓN)
                // ============================================================

                // 1. Caja contenedora ROJA (para ver si ocupa espacio)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .background(Color.White.copy(alpha = 0.2f)) // Fondo rojo suave
                ) {

                    // 2. Cargamos la animación directamente aquí
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.diente))

                    if (composition == null) {
                        // Si sale esto, el archivo JSON está mal
                        Text("⚠️ ERROR: Cargando 'diente.json'...", fontWeight = FontWeight.Bold)
                    } else {
                        // 3. Caja de animación AMARILLA
                        LottieAnimation(
                            composition = composition,
                            iterations = LottieConstants.IterateForever, // Se mueve por siempre
                            modifier = Modifier
                                .size(200.dp)
                                .background(Color.White) // Fondo amarillo chillón
                        )
                    }

                }

                // Espacio extra para el scroll
                Spacer(Modifier.height(100.dp))
            }
        }
    }
}