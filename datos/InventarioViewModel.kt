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

    fun obtenerEquiposPorDepartamento(idDepto: Int) {
        viewModelScope.launch {
            try {
                mensajeEstado.value = "Descargando inventario del área..."

                // 1. Descargamos de tu PC filtrando por área
                val respuestaRemota = RetrofitClient.api.obtenerEquiposPorDepto(idDepto)

                // 2. Aquí debes convertir y guardar en Room usando equipoDao.guardarEquipos()

                // 3. Mostramos en pantalla
                listaEquipos.clear()
                listaEquipos.addAll(respuestaRemota)
                mensajeEstado.value = ""

            } catch (e: Exception) {
                // Modo Offline: Rescatamos los datos del teléfono para esta área específica
                try {
                    val datosLocales = equipoDao.obtenerPorDepartamento(idDepto)
                    if (datosLocales.isNotEmpty()) {
                        listaEquipos.clear()
                        // Convertir EquipoLocal a Equipo y mostrar...
                        mensajeEstado.value = "Modo Offline activo"
                    } else {
                        mensajeEstado.value = "Sin conexión y sin datos para esta área."
                    }
                } catch (dbError: Exception) {
                    mensajeEstado.value = "Error interno: ${dbError.message}"
                }
            }
        }
    }
}