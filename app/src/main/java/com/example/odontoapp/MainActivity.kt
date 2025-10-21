package com.example.odontoapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.odontoapp.ui.theme.OdontoAppTheme
import com.example.odontoapp.view.AppNavHost   // ⬅️ importa tu NavHost

class MainActivity : ComponentActivity() {

    // Permiso para notificaciones (Android 13+)
    private val askNotifPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* no-op */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Canal de notificaciones (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "reminders",
                "Recordatorios",
                NotificationManager.IMPORTANCE_HIGH
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        // Solicitar permiso en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                askNotifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Carga la app con navegación
        setContent {
            OdontoAppTheme {
                AppNavHost()    // ⬅️ aquí se renderiza Home, formularios, etc.
            }
        }
    }
}
