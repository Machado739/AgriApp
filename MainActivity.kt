package com.example.agrimexapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agrimexapp.datos.InventarioViewModel
import com.example.agrimexapp.datos.LoginViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setContent es el "lienzo" donde Compose dibujará la pantalla
        setContent {
            MaterialTheme {
                // Aquí llamamos a nuestro diseño
                PantallaInventario()
            }
        }
    }
}

@Composable
fun PantallaInventario(viewModel: InventarioViewModel = viewModel()) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Inventario Sistemas - Agrimex", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))

        // Al hacer clic, disparamos la petición a la red local
        Button(onClick = { viewModel.obtenerEquiposDesdeServidor() }) {
            Text(text = "Consultar Equipos en Servidor")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Si existe un mensaje de estado, lo dibujamos en pantalla
        if (viewModel.mensajeEstado.value.isNotEmpty()) {
            Text(
                text = viewModel.mensajeEstado.value,
                color = androidx.compose.ui.graphics.Color.Red,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Esta lista dibujará tarjetas por cada computadora encontrada
        LazyColumn {
            items(viewModel.listaEquipos) { equipo ->
                Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Marca: ${equipo.marca}", fontWeight = FontWeight.Bold)
                        Text(text = "Modelo: ${equipo.modelo}")
                        Text(text = "Estado: ${equipo.estado}")
                    }
                }
            }
        }
    }
}

@Composable
fun AgrimexAppNavegacion() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            val loginViewModel: LoginViewModel = viewModel()
            // Llamamos a la pantalla de diseño que armamos antes
            PantallaLogin(
                viewModel = loginViewModel,
                onLoginExitoso = { esAdmin, idDepto ->
                    if (esAdmin) {
                        navController.navigate("menu_global_admin") // Acceso a Test y Sistemas
                    } else {
                        // Enviamos a Jorge y Alexis directo a su área (Ej: id_departamento = 1)
                        navController.navigate("panel_sistemas/$idDepto")
                    }
                }
            )
        }

        // Aquí irán tus otras pantallas
        composable("menu_global_admin") { Text("Bienvenido Admin - Selector de Áreas") }
        composable("panel_sistemas/{idDepto}") { Text("Bienvenido a Sistemas") }
    }
}