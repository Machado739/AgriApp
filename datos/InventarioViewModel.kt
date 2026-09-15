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
                mensajeEstado.value = "Sincronizando con el servidor..."

                // 1. Petición remota al servidor (FastAPI)
                val respuestaRemota = RetrofitClient.api.obtenerEquiposPorDepto(idDepto)

                // 2. Mapeo e Inserción en Room (Guarda la copia en SQLite)
                val listaLocales = respuestaRemota.map { equipoRemoto ->
                    EquipoLocal(
                        id_equipo = equipoRemoto.id_equipo,
                        marca = equipoRemoto.marca,
                        modelo = equipoRemoto.modelo,
                        numero_serie = equipoRemoto.numero_serie,
                        estado = equipoRemoto.estado,
                        id_departamento = equipoRemoto.id_departamento ?: idDepto
                    )
                }

                // Guardado físico en el almacenamiento interno del teléfono
                equipoDao.guardarEquipos(listaLocales)

                // 3. Renderizado en la interfaz gráfica
                listaEquipos.clear()
                listaEquipos.addAll(respuestaRemota)
                mensajeEstado.value = ""

            } catch (e: Exception) {
                // MODO OFFLINE: Si la API no responde, lee directamente desde SQLite
                try {
                    val datosLocales = equipoDao.obtenerPorDepartamento(idDepto)
                    if (datosLocales.isNotEmpty()) {
                        listaEquipos.clear()
                        val adaptados = datosLocales.map { local ->
                            Equipo(
                                id_equipo = local.id_equipo,
                                marca = local.marca,
                                modelo = local.modelo,
                                numero_serie = local.numero_serie,
                                estado = local.estado,
                                id_departamento = local.id_departamento
                            )
                        }
                        listaEquipos.addAll(adaptados)
                        mensajeEstado.value = "Modo Offline: Mostrando datos guardados"
                    } else {
                        mensajeEstado.value = "Sin datos guardados localmente para esta área"
                    }
                } catch (dbError: Exception) {
                    mensajeEstado.value = "Error al leer almacenamiento local: ${dbError.message}"
                }
            }
        }
    }
}