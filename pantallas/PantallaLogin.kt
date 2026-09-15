package com.example.agrimexapp.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.agrimexapp.datos.LoginViewModel

@Composable
fun PantallaLogin(
    viewModel: LoginViewModel,
    // Esta función inyectada redirigirá la navegación al tener éxito
    onLoginExitoso: (Boolean, Int?) -> Unit
) {
    var nombreUsuario by remember { mutableStateOf("") }
    var credenciales by remember { mutableStateOf("") }
    val mensajeEstado by viewModel.mensajeEstado

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Acceso Agrimex", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = nombreUsuario,
            onValueChange = { nombreUsuario = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = credenciales,
            onValueChange = { credenciales = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { viewModel.validarCredenciales(nombreUsuario, credenciales, onLoginExitoso) }) {
            Text("Ingresar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (mensajeEstado.isNotEmpty()) {
            Text(text = mensajeEstado, color = MaterialTheme.colorScheme.primary)
        }
    }
}