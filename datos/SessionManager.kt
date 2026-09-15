package com.example.agrimexapp.datos // Asegúrate que coincida con tu paquete

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

// Instancia global del almacenamiento en el teléfono
val Context.dataStore by preferencesDataStore(name = "sesion_agrimex")

class SessionManager(private val context: Context) {
    companion object {
        val ID_DEPARTAMENTO = intPreferencesKey("id_departamento")
        val ROL_ADMIN = booleanPreferencesKey("rol_admin")
        val ESTADO_LOGIN = booleanPreferencesKey("estado_login")
        val NOMBRE_USUARIO = stringPreferencesKey("nombre_usuario")
        val PUESTO_USUARIO = stringPreferencesKey("puesto_usuario")
    }

    suspend fun guardarSesion(idDepto: Int, esAdmin: Boolean, nombre: String, puesto: String) {
        context.dataStore.edit { preferencias ->
            preferencias[ID_DEPARTAMENTO] = idDepto
            preferencias[ROL_ADMIN] = esAdmin
            preferencias[ESTADO_LOGIN] = true
            preferencias[NOMBRE_USUARIO] = nombre
            preferencias[PUESTO_USUARIO] = puesto
        }
    }

    suspend fun leerSesionOffline(): Map<String, Any>? {
        val preferencias = context.dataStore.data.first()
        val estaLogueado = preferencias[ESTADO_LOGIN] ?: false

        return if (estaLogueado) {
            mapOf(
                "idDepto" to (preferencias[ID_DEPARTAMENTO] ?: 1),
                "esAdmin" to (preferencias[ROL_ADMIN] ?: false),
                "nombre" to (preferencias[NOMBRE_USUARIO] ?: "Usuario"),
                "puesto" to (preferencias[PUESTO_USUARIO] ?: "Personal")
            )
        } else {
            null
        }
    }
}