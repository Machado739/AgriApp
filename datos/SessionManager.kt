package com.example.agrimexapp.datos // Asegúrate que coincida con tu paquete

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

// Instancia global del almacenamiento en el teléfono
val Context.dataStore by preferencesDataStore(name = "sesion_agrimex")

class SessionManager(private val context: Context) {
    companion object {
        val ID_DEPARTAMENTO = intPreferencesKey("id_departamento")
        val ROL_ADMIN = booleanPreferencesKey("rol_admin")
        val ESTADO_LOGIN = booleanPreferencesKey("estado_login")
    }

    // 1. Guarda los datos de acceso cuando hay internet
    suspend fun guardarSesion(idDepto: Int, esAdmin: Boolean) {
        context.dataStore.edit { preferencias ->
            preferencias[ID_DEPARTAMENTO] = idDepto
            preferencias[ROL_ADMIN] = esAdmin
            preferencias[ESTADO_LOGIN] = true
        }
    }

    // 2. Lee los datos guardados cuando NO hay internet (Modo Offline)
    suspend fun leerSesionOffline(): Map<String, Any>? {
        val preferencias = context.dataStore.data.first()
        val estaLogueado = preferencias[ESTADO_LOGIN] ?: false

        return if (estaLogueado) {
            mapOf(
                "idDepto" to (preferencias[ID_DEPARTAMENTO] ?: 1),
                "esAdmin" to (preferencias[ROL_ADMIN] ?: false)
            )
        } else {
            null
        }
    }
}