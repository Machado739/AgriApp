package com.example.agrimexapp.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun MenuGlobalAdmin(onAreaSeleccionada: (Int) -> Unit){
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(text = "Panel de Administración", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text(text = "Selecciona un área para gestionar", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier =Modifier.height(40.dp))

        // Boton para acceder a sistemas (ID= = 1 en la base de datos)
        ElevatedButton(
            onClick = { onAreaSeleccionada(1) },
            modifier = Modifier.fillMaxWidth().height(60.dp)
        ) {
            Text(text = "Departamento de Sistemas", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        //Boton para acceder a Test (ID = 2 en la base de datos)
        ElevatedButton(
            onClick = { onAreaSeleccionada(2) },
            modifier = Modifier.fillMaxWidth().height(60.dp)
        ) {
            Text(text = "Departamento de Test", fontSize = 18.sp)
        }
    }
}