package com.example.odontoapp.view

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

fun Context.notifyNow(title: String, text: String) {
    val n = NotificationCompat.Builder(this, "reminders")
        .setSmallIcon(android.R.drawable.ic_popup_reminder)
        .setContentTitle(title)
        .setContentText(text)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()
    NotificationManagerCompat.from(this).notify((0..999999).random(), n)
}
