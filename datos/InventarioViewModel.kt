package com.example.agrimexapp.datos

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrimexapp.local.AgrimexDatabase
import com.example.agrimexapp.local.EquipoLocal
import kotlinx.coroutines.launch

class InventarioViewModel(application: Application) : AndroidViewModel(application) {

    val listaEquipos = mutableStateListOf<Equipo>()
    val mensajeEstado = mutableStateOf("")

    // Instancia del DAO de Room
    private val equipoDao = AgrimexDatabase.getDatabase(application).equipoDao()

    fun obtenerEquiposDesdeServidor() {
        viewModelScope.launch {
            try {
                mensajeEstado.value = "Sincronizando con el servidor..."

                // 1. Intentamos obtener los datos frescos desde tu PC (FastAPI)
                val respuestaRemota = RetrofitClient.api.obtenerEquipos()

                // 2. Convertimos la respuesta web al formato de la base de datos local (Room)
                val listaLocales = respuestaRemota.map { equipoRemoto ->
                    EquipoLocal(
                        id_equipo = equipoRemoto.id_equipo,
                        marca = equipoRemoto.marca,
                        modelo = equipoRemoto.modelo,
                        numero_serie = equipoRemoto.numero_serie,
                        estado = equipoRemoto.estado
                    )
                }

                // 3. Guardamos los datos en el teléfono para acceso offline
                equipoDao.guardarEquipos(listaLocales)

                // 4. Actualizamos la pantalla con los datos nuevos
                listaEquipos.clear()
                listaEquipos.addAll(respuestaRemota)
                mensajeEstado.value = "Datos actualizados y guardados localmente"

            } catch (e: Exception) {
                // Si la PC está apagada o no hay red, cargamos lo último que tenga Room guardado
                try {
                    val datosLocales = equipoDao.obtenerTodos()
                    if (datosLocales.isNotEmpty()) {
                        listaEquipos.clear()
                        val adaptados = datosLocales.map { local ->
                            Equipo(local.id_equipo, local.marca, local.modelo, local.numero_serie, local.estado)
                        }
                        listaEquipos.addAll(adaptados)
                        mensajeEstado.value = "Modo Offline: Mostrando datos guardados"
                    } else {
                        mensajeEstado.value = "Sin conexión y sin datos previos: ${e.message}"
                    }
                } catch (dbError: Exception) {
                    mensajeEstado.value = "Error: ${e.message}"
                }
            }
        }
    }
}