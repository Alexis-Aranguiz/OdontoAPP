package com.example.odontoapp.view.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ValidatedField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = error != null,
        trailingIcon = { if (error != null) Icon(Icons.Filled.Error, contentDescription = null) },
        modifier = modifier
    )
    AnimatedVisibility(visible = error != null) {
        Text(error ?: "", style = MaterialTheme.typography.labelSmall)
    }
    Spacer(Modifier.height(8.dp))
}
