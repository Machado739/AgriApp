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
import com.example.agrimexapp.ui.PantallaInicio

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

        // 1. Pantalla de Acceso (Login)
        composable("login") {
            val loginViewModel: LoginViewModel = viewModel()
            PantallaLogin(
                viewModel = loginViewModel,
                onLoginExitoso = { esAdmin, idDepto, nombreUsuario, puestoUsuario ->
                    // Navegamos a la pantalla de inicio pasando los datos del usuario para el menú lateral
                    navController.navigate("inicio/$nombreUsuario/$puestoUsuario/$idDepto") {
                        // Limpiamos el historial para que al dar "Atrás" no regrese al login
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // 2. Pantalla de Inicio con Menú Lateral Deslizante
        composable("inicio/{nombre}/{puesto}/{idDepto}") { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre") ?: "Usuario"
            val puesto = backStackEntry.arguments?.getString("puesto") ?: "Personal"
            val idDepto = backStackEntry.arguments?.getString("idDepto")?.toInt() ?: 1

            PantallaInicio(
                nombreUsuario = nombre,
                puestoUsuario = puesto,
                onNavegarEquipos = {
                    // Al hacer clic en "Equipos" en el menú, salta al inventario del área
                    navController.navigate("panel_departamento/$idDepto")
                },
                onCerrarSesion = {
                    // Al cerrar sesión, regresa al login y limpia todo el flujo previo
                    navController.navigate("login") {
                        popUpTo("inicio/{nombre}/{puesto}/{idDepto}") { inclusive = true }
                    }
                }
            )
        }

        // 3. Pantalla de Inventario Completo del Departamento (Sistemas / Test)
        composable("panel_departamento/{idDepto}") { backStackEntry ->
            val idDeptoStr = backStackEntry.arguments?.getString("idDepto") ?: "1"
            val idDepto = idDeptoStr.toInt()

            val inventarioViewModel: InventarioViewModel = viewModel()

            PantallaInventario(
                viewModel = inventarioViewModel,
                idDepartamento = idDepto
            )
        }
    }
}