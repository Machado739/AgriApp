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
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agrimexapp.datos.InventarioViewModel
import com.example.agrimexapp.datos.LoginViewModel
import com.example.agrimexapp.pantallas.MenuGlobalAdmin
import com.example.agrimexapp.pantallas.PantallaLogin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setContent es el "lienzo" donde Compose dibujará la pantalla
        setContent {
            MaterialTheme {
                // Aquí llamamos a nuestro diseño
                AgrimexAppNavegacion()
            }
        }
    }
}

@Composable
fun PantallaInventario(viewModel: InventarioViewModel = viewModel(), idDepartamento: Int) {
    LaunchedEffect(idDepartamento) {
        viewModel.obtenerEquiposPorDepartamento(idDepartamento)
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (idDepartamento == 1) "Inventario Sistemas" else "Inventario Test",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

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
        // En tu MainActivity (dentro del NavHost):
        composable("login") {
            PantallaLogin(viewModel = viewModel()) { esAdmin, idDepto ->
                if (esAdmin) {
                    navController.navigate("menu_global_admin")
                } else {
                    navController.navigate("panel_departamento/$idDepto")
                }
            }
        }

        // Aquí irán tus otras pantallas
        composable("menu_global_admin") {
            MenuGlobalAdmin(
                onAreaSeleccionada = { idDepto ->
                    navController.navigate("panel_departamento/$idDepto")
                }
            )
        }
        composable("panel_departamento/{idDepto}") { backStackEntry ->
            //1. Extraemos el numero de la ruta (1 para Sistemas, 2 para Test, etc.)
            val idDeptoStr = backStackEntry.arguments?.getString("idDepto") ?: "1"
            val idDepto = idDeptoStr.toInt()

            val inventarioViewModel: InventarioViewModel = viewModel()

            PantallaInventario(viewModel = inventarioViewModel, idDepartamento = idDepto)
/*
            if (it.arguments?.getString("idDepto")?.toIntOrNull() == 1) {
                PantallaInventario(viewModel = viewModel())
            } else {
                // Aquí podrías mostrar otra pantalla para otros departamentos
                Text(text = "Pantalla para otro departamento")
            }*/
        }
    }
}