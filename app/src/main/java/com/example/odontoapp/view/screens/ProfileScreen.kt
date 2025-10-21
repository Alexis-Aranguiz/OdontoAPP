package com.example.odontoapp.view.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

    var picked by remember { mutableStateOf<Uri?>(vm.photoUri?.let(Uri::parse)) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        picked = uri; vm.onPhoto(uri?.toString())
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Mi perfil") }) }) { p ->
        Column(Modifier.fillMaxSize().padding(p).padding(16.dp)) {
            ElevatedCard(Modifier.fillMaxWidth().animateContentSize()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Foto de perfil")
                    Spacer(Modifier.height(8.dp))
                    if (picked != null)
                        Image(
                            painter = rememberAsyncImagePainter(picked),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().height(160.dp),
                            contentScale = ContentScale.Crop
                        )
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = {
                        picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }) { Text("Elegir desde galería") }
                }
            }
            Spacer(Modifier.height(16.dp))
            ValidatedField(vm.name, vm::onName, "Nombre", vm.nameErr)
            ValidatedField(vm.email, vm::onEmail, "Email", vm.emailErr)
            ValidatedField(vm.phone, vm::onPhone, "Teléfono", vm.phoneErr)
            Button(onClick = { vm.save() }, enabled = vm.canSave, modifier = Modifier.fillMaxWidth()) { Text("Guardar") }
        }
    }
}
